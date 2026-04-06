package com.qpang.deliverymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.qpang.deliverymanager",
        "com.qpang.hub.infrastructure"
})
@EntityScan(basePackages = {
        "com.qpang.deliverymanager.domain",
        "com.qpang.hub.domain"
})
@EnableJpaRepositories(basePackages = {
        "com.qpang.deliverymanager.infrastructure",
        "com.qpang.hub.infrastructure"
})
public class DeliveryManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryManagerApplication.class, args);
    }
}