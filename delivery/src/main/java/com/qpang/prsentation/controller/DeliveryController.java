package com.qpang.prsentation.controller;

import com.qpang.application.service.DeliveryService;
import com.qpang.prsentation.dto.CreateDeliveryRequest;
import com.qpang.prsentation.dto.CreateDeliveryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public CreateDeliveryResponse createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        return deliveryService.createDelivery(request);
    }
}