package com.qpang.company;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret.key=VEVTVF9VUl9DQ01QQU5ZX1NFQ1JFVF9LRVlfMDAwMDAwMDAwMDA="
})
class CompanyApplicationTests {

    @Test
    void contextLoads() {
    }

}
