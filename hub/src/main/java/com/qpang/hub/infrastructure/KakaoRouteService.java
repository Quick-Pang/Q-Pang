package com.qpang.hub.infrastructure;

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
                throw new IllegalStateException("Kakao API 응답에 routes가 없습니다.");
            }

            List<Map<String, Object>> routes = (List<Map<String, Object>>) body.get("routes");
            if (routes == null || routes.isEmpty()) {
                throw new IllegalStateException("Kakao API routes가 비어 있습니다.");
            }

            Map<String, Object> route = routes.get(0);
            Map<String, Object> summary = (Map<String, Object>) route.get("summary");
            if (summary == null) {
                throw new IllegalStateException("Kakao API summary가 없습니다.");
            }

            Object distanceObj = summary.get("distance");
            Object durationObj = summary.get("duration");

            if (!(distanceObj instanceof Number distanceNumber)) {
                throw new IllegalStateException("Kakao API distance 값이 없습니다.");
            }

            if (!(durationObj instanceof Number durationNumber)) {
                throw new IllegalStateException("Kakao API duration 값이 없습니다.");
            }

            BigDecimal distanceKm = BigDecimal
                    .valueOf(distanceNumber.doubleValue())
                    .divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);

            int durationMin = durationNumber.intValue() / 60;

            if (distanceKm.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Kakao API distance 값이 0 이하입니다.");
            }

            if (durationMin <= 0) {
                throw new IllegalStateException("Kakao API duration 값이 0 이하입니다.");
            }

            return new RouteInfo(distanceKm, durationMin);

        } catch (ResourceAccessException e) {
            throw new IllegalStateException("Kakao API timeout 또는 네트워크 오류", e);
        } catch (RestClientException e) {
            throw new IllegalStateException("Kakao API 호출 실패", e);
        }
    }

    public record RouteInfo(BigDecimal distance, int duration) {}
}