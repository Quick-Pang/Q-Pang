package com.qpang.common.security;

import com.qpang.common.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String TOKEN_TYPE_KEY = "token_type";
    public static final String ACCESS_TOKEN_TYPE = "ACCESS";
    public static final String REFRESH_TOKEN_TYPE = "REFRESH";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_VALID_TIME = 60 * 60 * 1000L;
    public static final long REFRESH_TOKEN_VALID_TIME = 14L * 24 * 60 * 60 * 1000L;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(String username, UserRole role) {
        return createAccessToken(username, role, Duration.ofMillis(ACCESS_TOKEN_VALID_TIME));
    }

    public String createAccessToken(String username, UserRole role, Duration validity) {
        Date date = new Date();
        return BEARER_PREFIX +
                Jwts.builder()
                        .subject(username)
                        .claim(AUTHORIZATION_KEY, role.name())
                        .claim(TOKEN_TYPE_KEY, ACCESS_TOKEN_TYPE)
                        .expiration(new Date(date.getTime() + validity.toMillis()))
                        .issuedAt(date)
                        .signWith(key)
                        .compact();
    }

    public String createRefreshToken(String username, UserRole role) {
        Date date = new Date();
        return BEARER_PREFIX +
                Jwts.builder()
                        .subject(username)
                        .claim(AUTHORIZATION_KEY, role.name())
                        .claim(TOKEN_TYPE_KEY, REFRESH_TOKEN_TYPE)
                        .expiration(new Date(date.getTime() + REFRESH_TOKEN_VALID_TIME))
                        .issuedAt(date)
                        .signWith(key)
                        .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public Date getExpirationFromToken(String token) {
        return getUserInfoFromToken(token).getExpiration();
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
