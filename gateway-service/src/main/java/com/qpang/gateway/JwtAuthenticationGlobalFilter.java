
package com.qpang.gateway;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;
@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {
    private static final String BEARER_PREFIX = "Bearer ";
    @Value("${jwt.secret.key}")
    private String secretKeyBase64;
    private SecretKey key;
    
    //JWT 없이 연결할 경로
    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/actuator",
            "/users/login",
            "/users/signup"
    );
    @PostConstruct
    void initKey() {
        byte[] bytes = Base64.getDecoder().decode(secretKeyBase64.trim());
        key = Keys.hmacShaKeyFor(bytes);
    }
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
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
    private boolean isPublicPath(String path) {
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
