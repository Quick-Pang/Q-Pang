package com.qpang.common.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = JwtUtilPropertyTest.TestConfig.class)
@DisplayName("JwtUtil 프로퍼티 주입 테스트")
class JwtUtilPropertyTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Configuration
    @Import(JwtUtil.class)
    static class TestConfig {
    }

    @Test
    @DisplayName("application.yaml의 jwt.secret.key 주입 확인")
    void verifyPropertyInjection() {
        // application.yaml 파일에 정의된 jwt.secret.key 값이 JwtUtil 클래스의 secretKey 필드에 정상적으로 주입되었는지 확인합니다.
        
        // when
        String secretKey = (String) ReflectionTestUtils.getField(jwtUtil, "secretKey");
        log.info("Injected Secret Key (first 10 chars): {}...", (secretKey != null && secretKey.length() > 10) ? secretKey.substring(0, 10) : secretKey);

        // then
        // application.yaml에 설정된 값: A1b2C3d4E5f6G7h8I9j0K1l2M3n4O5p6Q7r8S9t0U1v2W3x4Y5z6A1b2C3d4E5f6
        assertEquals("A1b2C3d4E5f6G7h8I9j0K1l2M3n4O5p6Q7r8S9t0U1v2W3x4Y5z6A1b2C3d4E5f6", secretKey);
    }
}
