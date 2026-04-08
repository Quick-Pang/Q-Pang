package com.qpang.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
@SpringBootApplication
@ComponentScan(basePackages = {"com.qpang.hub", "com.qpang.common"})
public class HubApplication {

    public static void main(String[] args) {
        SpringApplication.run(HubApplication.class, args);
    }

}
