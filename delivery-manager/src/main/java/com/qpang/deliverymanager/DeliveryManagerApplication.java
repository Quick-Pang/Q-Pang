package com.qpang.deliverymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.domain.AuditorAware;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.qpang.hub.api.client")
@SpringBootApplication
@EntityScan(basePackages = {
        "com.qpang.deliverymanager.domain",
        "com.qpang.common.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.qpang.deliverymanager.infrastructure"
})
public class DeliveryManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryManagerApplication.class, args);
    }

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return Optional::empty;
    }
}
