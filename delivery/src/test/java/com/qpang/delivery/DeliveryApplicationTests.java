package com.qpang.delivery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret.key=VEVTVF9ERUxJVkVSWV9TRUNSRVRfS0VZXzAwMDAwMDAw",
        "slack.webhook-url=http://localhost/test"
})
class DeliveryApplicationTests {

    @Test
    void contextLoads() {
    }

}
