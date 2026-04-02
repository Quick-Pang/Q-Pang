package com.qpang.common.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = JwtUtilPropertyTest.TestConfig.class)
@TestPropertySource(properties = "jwt.secret.key=VEVTVF9URVNUX0tFWV9QUk9QRVJUWV9URVNUX1NFQ1JFVF9LRVlfMTIzNDU2Nzg5MA==")
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
        // 테스트 전용 프로퍼티가 JwtUtil에 정상 주입되는지 확인합니다.
        String secretKey = (String) ReflectionTestUtils.getField(jwtUtil, "secretKey");

        assertNotNull(secretKey);
        assertEquals("VEVTVF9URVNUX0tFWV9QUk9QRVJUWV9URVNUX1NFQ1JFVF9LRVlfMTIzNDU2Nzg5MA==", secretKey);
    }
}
