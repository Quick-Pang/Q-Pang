package com.qpang.hub.infrastructure;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoRouteService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public RouteInfo getRoute(double srcLon, double srcLat, double dstLon, double dstLat) {
        String url = "https://apis-navi.kakaomobility.com/v1/directions?"
                + "origin=" + srcLon + "," + srcLat
                + "&destination=" + dstLon + "," + dstLat
                + "&priority=DISTANCE";

        URI uri = URI.create(url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> response =
                    restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("routes")) {
                throw new CustomException(CommonErrorCode.INVALID_KAKAO_ROUTE_RESPONSE);
            }

            List<Map<String, Object>> routes = (List<Map<String, Object>>) body.get("routes");
            if (routes == null || routes.isEmpty()) {
                throw new CustomException(CommonErrorCode.INVALID_KAKAO_ROUTE_RESPONSE);
            }

            Map<String, Object> route = routes.get(0);
            Map<String, Object> summary = (Map<String, Object>) route.get("summary");
            if (summary == null) {
                throw new CustomException(CommonErrorCode.INVALID_KAKAO_ROUTE_RESPONSE);
            }

            Object distanceObj = summary.get("distance");
            Object durationObj = summary.get("duration");

            if (!(distanceObj instanceof Number distanceNumber)) {
                throw new CustomException(CommonErrorCode.INVALID_KAKAO_ROUTE_RESPONSE);
            }

            if (!(durationObj instanceof Number durationNumber)) {
                throw new CustomException(CommonErrorCode.INVALID_KAKAO_ROUTE_RESPONSE);
            }

            BigDecimal distanceKm = BigDecimal
                    .valueOf(distanceNumber.doubleValue())
                    .divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);

            int durationMin = durationNumber.intValue() / 60;

            if (distanceKm.compareTo(BigDecimal.ZERO) <= 0) {
                throw new CustomException(CommonErrorCode.INVALID_KAKAO_ROUTE_RESPONSE);
            }

            if (durationMin <= 0) {
                throw new CustomException(CommonErrorCode.INVALID_KAKAO_ROUTE_RESPONSE);
            }

            return new RouteInfo(distanceKm, durationMin);

        } catch (ResourceAccessException e) {
            throw new CustomException(CommonErrorCode.KAKAO_ROUTE_API_ERROR);
        } catch (RestClientException e) {
            throw new CustomException(CommonErrorCode.KAKAO_ROUTE_API_ERROR);
        }
    }

    public record RouteInfo(BigDecimal distance, int duration) {}
}