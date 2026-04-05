package com.qpang.hub.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
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

        ResponseEntity<Map> response =
                restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        Map<String, Object> body = response.getBody();

        if (body == null || !body.containsKey("routes")) {
            return new RouteInfo(BigDecimal.ZERO, 0);
        }

        List<Map<String, Object>> routes = (List<Map<String, Object>>) body.get("routes");
        if (routes == null || routes.isEmpty()) {
            return new RouteInfo(BigDecimal.ZERO, 0);
        }

        Map<String, Object> route = routes.get(0);
        Map<String, Object> summary = (Map<String, Object>) route.get("summary");
        if (summary == null) {
            return new RouteInfo(BigDecimal.ZERO, 0);
        }

        BigDecimal distanceKm = BigDecimal
                .valueOf(((Number) summary.get("distance")).doubleValue())
                .divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);

        int durationMin = ((Number) summary.get("duration")).intValue() / 60;

        return new RouteInfo(distanceKm, durationMin);
    }

    public record RouteInfo(BigDecimal distance, int duration) {}
}