package com.qpang.orderservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "delivery-service", path = "/deliveries")
public interface DeliveryClient {

    @PostMapping
    CreateDeliveryResponse create(
            @RequestBody CreateDeliveryRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole);


    record CreateDeliveryRequest(UUID orderId, UUID supplyCompanyId, UUID requestCompanyId) {}


    record CreateDeliveryResponse(UUID deliveryId, UUID orderId, String deliveryStatus) {}
}
