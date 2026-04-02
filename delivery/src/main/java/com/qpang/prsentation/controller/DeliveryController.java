package com.qpang.prsentation.controller;

import com.qpang.application.service.DeliveryService;
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

    //배송 생성todo: 권한 검증
    @PostMapping
    public CreateDeliveryResponse createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        return deliveryService.createDelivery(request);
    }

    // 배송 단건 조회todo: 권한 검증
    @GetMapping("/{deliveryId}")
    public GetDeliveryResponse getDelivery(@PathVariable UUID deliveryId) {
        return deliveryService.getDelivery(deliveryId);
    }

    //배송 목록 조회 todo: 권한 검증
    @GetMapping
    public List<GetDeliveryListResponse> getDeliveries() {
        return deliveryService.getDeliveries();
    }

    //배송 경로 조회todo: 권한 검증
    @GetMapping("/{deliveryId}/routes")
    public List<GetDeliveryRouteResponse> getDeliveryRoutes(@PathVariable UUID deliveryId) {
        return deliveryService.getDeliveryRoutes(deliveryId);
    }

    //출발 허브 기준 배송 목록 조회 todo: 권한 검증
    @GetMapping("/source-hubs/{sourceHubId}")
    public List<GetDeliveryListResponse> getDeliveriesBySourceHub(@PathVariable UUID sourceHubId) {
        return deliveryService.getDeliveriesBySourceHub(sourceHubId);
    }

    //도착 허브 기준 배송 목록 조회 todo: 권한 검증
    @GetMapping("/dest-hubs/{destHubId}")
    public List<GetDeliveryListResponse> getDeliveriesByDestHub(@PathVariable UUID destHubId) {
        return deliveryService.getDeliveriesByDestHub(destHubId);
    }

    //현재 진행 진행 경로 확인
    @GetMapping("/{deliveryId}/current-route")
    public GetCurrentDeliveryRouteResponse getCurrentDeliveryRoute(@PathVariable UUID deliveryId) {
        return deliveryService.getCurrentDeliveryRoute(deliveryId);
    }

    //배송 정보 수정todo: 권한 검증
    @PatchMapping("/{deliveryId}")
    public void updateDelivery(
            @PathVariable UUID deliveryId,
            @Valid @RequestBody UpdateDeliveryRequest request
    ) {
        deliveryService.updateDelivery(deliveryId, request);
    }

    //배송 상태 수정 todo: 권한 검증
    @PatchMapping("/{deliveryId}/status")
    public void updateDeliveryStatus(
            @PathVariable UUID deliveryId,
            @Valid @RequestBody UpdateDeliveryStatusRequest request
    ) {
        deliveryService.updateDeliveryStatus(deliveryId, request.getDeliveryStatus());
    }

    //배송 경로 상태 수정todo: 권한 검증
    @PatchMapping("/routes/{deliveryRouteId}/status")
    public void updateDeliveryRouteStatus(
            @PathVariable UUID deliveryRouteId,
            @Valid @RequestBody UpdateDeliveryRouteStatusRequest request
    ) {
        deliveryService.updateDeliveryRouteStatus(deliveryRouteId, request);
    }

    //배송 삭제 todo: 권한 검증, 삭제된 배송 조회 안되도록 설정
    @DeleteMapping("/{deliveryId}")
    public void deleteDelivery(@PathVariable UUID deliveryId) {
        UUID deletedBy = UUID.fromString("00000000-0000-0000-0000-000000000001");
        deliveryService.deleteDelivery(deliveryId, deletedBy);
    }
}