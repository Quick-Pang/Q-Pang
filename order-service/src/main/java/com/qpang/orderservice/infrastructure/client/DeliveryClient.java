package com.qpang.orderservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "delivery", path = "/deliveries")
public interface DeliveryClient {

    @PostMapping
    CreateDeliveryResponse create(@RequestBody CreateDeliveryRequest request);


    record CreateDeliveryRequest(UUID orderId, UUID supplyCompanyId, UUID requestCompanyId) {}


    record CreateDeliveryResponse(UUID id, UUID orderId, String deliveryStatus) {}
}
