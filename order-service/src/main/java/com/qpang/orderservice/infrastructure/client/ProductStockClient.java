package com.qpang.orderservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", path = "/products")
public interface ProductStockClient {

    @PostMapping("/{id}/stock/decrease")
    void decreaseStock(@PathVariable("id") UUID productId, @RequestBody ProductStockFeignRequest body);

    @PostMapping("/{id}/stock/increase")
    void increaseStock(@PathVariable("id") UUID productId, @RequestBody ProductStockFeignRequest body);
}
