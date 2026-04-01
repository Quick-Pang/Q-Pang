package com.qpang.common.security;

import com.qpang.common.entity.UserRole;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DisplayName("JwtUtil Unit Test")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set a sample secret key for testing (Base64 encoded "this-is-a-sample-secret-key-with-at-least-256-bits-strength")
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "dGhpcy1pcy1hLXNhbXBsZS1zZWNyZXQta2V5LXdpdGgtYXQtbGVhc3QtMjU2LWJpdHMtc3RyZW5ndGg=");
        jwtUtil.init();
    }

    @Test
    @DisplayName("Create Token and Validate")
    void createToken_Success() {
        // 토큰 생성 및 유효성 검증 테스트
        // given
        String username = "testUser";
        UserRole role = UserRole.MASTER;

        // when
        String tokenValue = jwtUtil.createToken(username, role);
        log.info("Generated Token Value: {}", tokenValue);
        String token = jwtUtil.substringToken(tokenValue);

        // then
        assertNotNull(tokenValue);
        assertTrue(tokenValue.startsWith(JwtUtil.BEARER_PREFIX));
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("Extract Claims from Token")
    void getClaims_Success() {
        // 토큰에서 클레임(사용자 정보) 추출 테스트
        // given
        String username = "testUser";
        UserRole role = UserRole.MASTER;
        String tokenValue = jwtUtil.createToken(username, role);
        String token = jwtUtil.substringToken(tokenValue);

        // when
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        log.info("Extracted Claims - Subject: {}, Auth: {}", claims.getSubject(), claims.get(JwtUtil.AUTHORIZATION_KEY));

        // then
        assertEquals(username, claims.getSubject());
        assertEquals(role.name(), claims.get(JwtUtil.AUTHORIZATION_KEY));
    }

    @Test
    @DisplayName("Invalid Token Validation")
    void validateToken_Fail() {
        // 잘못된 토큰(조작된 서명)에 대한 유효성 검증 실패 테스트
        // given
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImF1dGgiOiJDVVNUT01FUiIsImV4cCI6MTcwMDAwMDAwMCwiaWF0IjoxNzAwMDAwMDAwfQ.invalid_signature";

        // when
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // then
        assertFalse(isValid);
    }
}
