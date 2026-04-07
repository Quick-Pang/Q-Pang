package com.qpang.prsentation.controller;

import com.qpang.application.service.DeliveryService;
import com.qpang.infrastructure.client.dto.CreateDeliveryCommand;
import com.qpang.prsentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public CreateDeliveryResponse createDelivery(
            @Valid @RequestBody CreateDeliveryCommand request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        return deliveryService.createDelivery(request, userId, userRole);
    }

    @GetMapping("/{deliveryId}")
    public GetDeliveryResponse getDelivery(
            @PathVariable UUID deliveryId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        return deliveryService.getDelivery(deliveryId, userId, userRole);
    }

    @GetMapping
    public List<GetDeliveryListResponse> getDeliveries(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        return deliveryService.getDeliveries(userId, userRole);
    }

    @GetMapping("/{deliveryId}/routes")
    public List<GetDeliveryRouteResponse> getDeliveryRoutes(
            @PathVariable UUID deliveryId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        return deliveryService.getDeliveryRoutes(deliveryId, userId, userRole);
    }

    @GetMapping("/source-hubs/{sourceHubId}")
    public List<GetDeliveryListResponse> getDeliveriesBySourceHub(
            @PathVariable UUID sourceHubId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        return deliveryService.getDeliveriesBySourceHub(sourceHubId, userId, userRole);
    }

    @GetMapping("/dest-hubs/{destHubId}")
    public List<GetDeliveryListResponse> getDeliveriesByDestHub(
            @PathVariable UUID destHubId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        return deliveryService.getDeliveriesByDestHub(destHubId, userId, userRole);
    }

    @GetMapping("/{deliveryId}/current-route")
    public GetCurrentDeliveryRouteResponse getCurrentDeliveryRoute(
            @PathVariable UUID deliveryId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        return deliveryService.getCurrentDeliveryRoute(deliveryId, userId, userRole);
    }

    @PatchMapping("/{deliveryId}")
    public void updateDelivery(
            @PathVariable UUID deliveryId,
            @Valid @RequestBody UpdateDeliveryRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        deliveryService.updateDelivery(deliveryId, request, userId, userRole);
    }

    @PatchMapping("/{deliveryId}/status")
    public void updateDeliveryStatus(
            @PathVariable UUID deliveryId,
            @Valid @RequestBody UpdateDeliveryStatusRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        deliveryService.updateDeliveryStatus(deliveryId, request.getDeliveryStatus(), userId, userRole);
    }

    @PatchMapping("/routes/{deliveryRouteId}/status")
    public void updateDeliveryRouteStatus(
            @PathVariable UUID deliveryRouteId,
            @Valid @RequestBody UpdateDeliveryRouteStatusRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        deliveryService.updateDeliveryRouteStatus(deliveryRouteId, request, userId, userRole);
    }

    @DeleteMapping("/{deliveryId}")
    public void deleteDelivery(
            @PathVariable UUID deliveryId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole
    ) {
        deliveryService.deleteDelivery(deliveryId, userId, userRole);
    }
}