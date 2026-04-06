package com.qpang.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.qpang.userservice.infrastructure.external")
@ComponentScan(basePackages = {
        "com.qpang.userservice",
        "com.qpang.common"
})
@EntityScan(basePackages = {
        "com.qpang.userservice.domain.entity",
        "com.qpang.common.entity"
})
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
