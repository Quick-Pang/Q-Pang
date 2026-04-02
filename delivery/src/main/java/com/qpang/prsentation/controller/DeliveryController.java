package com.qpang.prsentation.controller;

import com.qpang.application.service.DeliveryService;
import com.qpang.domain.model.Delivery;
import com.qpang.prsentation.dto.CreateRequest;
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
    public ResponseEntity<String> createDelivery(@RequestBody CreateRequest request) {
        return ResponseEntity.ok("요청 받기 성공");
    }
}
