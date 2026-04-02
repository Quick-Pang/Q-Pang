package com.qpang.prsentation.controller;

import com.qpang.application.service.DeliveryService;
import com.qpang.domain.model.Delivery;
import com.qpang.prsentation.dto.CreateDeliveryRequest;
import com.qpang.prsentation.dto.CreateDeliveryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<CreateDeliveryResponse> createDelivery(@RequestBody CreateDeliveryRequest request) {
        Delivery savedDelivery = deliveryService.createDelivery(request);
        CreateDeliveryResponse response = CreateDeliveryResponse.from(savedDelivery);
        return ResponseEntity.ok(response);
    }
}
