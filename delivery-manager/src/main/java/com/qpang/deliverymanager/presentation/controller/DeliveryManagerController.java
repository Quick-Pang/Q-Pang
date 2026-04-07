package com.qpang.deliverymanager.presentation.controller;

import com.qpang.deliverymanager.application.dto.DeliveryManagerResult;
import com.qpang.deliverymanager.application.service.DeliveryManagerService;
import com.qpang.deliverymanager.presentation.dto.DeliveryManagerCreateRequest;
import com.qpang.deliverymanager.presentation.dto.DeliveryManagerResponse;
import com.qpang.deliverymanager.presentation.dto.DeliveryManagerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-managers")
@RequiredArgsConstructor
public class DeliveryManagerController {

    private final DeliveryManagerService deliveryManagerService;

    @GetMapping
    public Page<DeliveryManagerResponse> getAll(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return deliveryManagerService.getAll(pageable)
                .map(DeliveryManagerResponse::from);
    }

    @PostMapping
    public ResponseEntity<DeliveryManagerResponse> create(
            @RequestBody DeliveryManagerCreateRequest request
    ) {
        DeliveryManagerResult result = deliveryManagerService.create(request.toCommand());
        return ResponseEntity.ok(DeliveryManagerResponse.from(result));
    }

    @GetMapping("/{id}")
    public DeliveryManagerResponse getById(@PathVariable UUID id) {
        return DeliveryManagerResponse.from(deliveryManagerService.getById(id));
    }

    @PatchMapping("/{id}")
    public DeliveryManagerResponse update(
            @PathVariable UUID id,
            @RequestBody DeliveryManagerUpdateRequest request
    ) {
        return DeliveryManagerResponse.from(
                deliveryManagerService.update(id, request.toCommand())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id,@RequestParam UUID userId) {
        deliveryManagerService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}