package com.qpang.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_CLAIM = "auth";

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";

    private static final String USER_SERVICE_ID = "user-service";
    private static final Duration LOOKUP_TIMEOUT = Duration.ofSeconds(5);

    @Value("${jwt.secret.key}")
    private String secretKeyBase64;

    private SecretKey key;

    private final WebClient webClient;

    public JwtAuthenticationGlobalFilter(@LoadBalanced WebClient.Builder loadBalancedWebClientBuilder) {
        this.webClient = loadBalancedWebClientBuilder.build();
    }

    @PostConstruct
    void initKey() {
        byte[] bytes = Base64.getDecoder().decode(secretKeyBase64.trim());
        key = Keys.hmacShaKeyFor(bytes);
    }

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/actuator",
            "/auth/sign-up"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }
        String path = request.getURI().getPath();
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith(BEARER_PREFIX)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        String token = auth.substring(BEARER_PREFIX.length()).trim();
        final Claims claims;
        try {
            claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String username = claims.getSubject();
        String role = claims.get(ROLE_CLAIM, String.class);
        if (username == null || username.isBlank() || role == null || role.isBlank()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host(USER_SERVICE_ID)
                        .path("/users/by-username/{username}/id")
                        .build(username))
                .header(HttpHeaders.AUTHORIZATION, auth)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(LOOKUP_TIMEOUT)
                .flatMap(node -> {
                    UUID userId;
                    try {
                        userId = UUID.fromString(node.get("data").get("userId").asText());
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                    ServerHttpRequest mutated = exchange.getRequest().mutate()
                            .headers(h -> {
                                h.remove(HEADER_USER_ID);
                                h.remove(HEADER_USER_ROLE);
                            })
                            .header(HEADER_USER_ID, userId.toString())
                            .header(HEADER_USER_ROLE, role)
                            .build();
                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
