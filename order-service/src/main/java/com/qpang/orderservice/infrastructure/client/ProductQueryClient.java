package com.qpang.orderservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.qpang.common.response.APIResponse;

@FeignClient(name = "product-service", path = "/products")
public interface ProductQueryClient {

    @GetMapping("/{id}")
    APIResponse<ProductHubResponse> getProduct(@PathVariable("id") UUID productId);

    record ProductHubResponse(UUID id, UUID hubId) {}
}
