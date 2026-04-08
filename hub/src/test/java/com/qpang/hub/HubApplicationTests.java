package com.qpang.hub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret.key=VEVTVF9VUl9IVUJTRUNSRVRfS0VZXzAwMDAwMDAwMDA=",
        "kakao.api.key=test-kakao-api-key"
})
class HubApplicationTests {

    @Test
    void contextLoads() {
    }

}
