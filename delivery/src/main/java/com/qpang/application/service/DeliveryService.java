package com.qpang.application.service;

import com.qpang.domain.enums.DeliveryRouteStatus;
import com.qpang.domain.enums.DeliveryStatus;
import com.qpang.domain.model.Delivery;
import com.qpang.domain.model.DeliveryRoute;
import com.qpang.prsentation.dto.*;
import com.qpang.repository.DeliveryRepository;
import com.qpang.repository.DeliveryRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private static final int FIRST_ROUTE_SEQUENCE = 1;
    private static final double DEFAULT_ESTIMATED_DISTANCE = 0.0;
    private static final int DEFAULT_ESTIMATED_TIME = 0;

    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteRepository deliveryRouteRepository;


    //배송 생성
    @Transactional
    public CreateDeliveryResponse createDelivery(CreateDeliveryRequest request) {
        Delivery delivery = Delivery.create(
                request.getOrderId(),
                request.getSourceHubId(),
                request.getDestHubId(),
                request.getDeliveryAddress(),
                request.getReceiverName(),
                request.getReceiverSlackId()
        );

        Delivery savedDelivery = deliveryRepository.save(delivery);

        List<DeliveryRoute> routes = createDeliveryRoutes(savedDelivery, request);
        deliveryRouteRepository.saveAll(routes);

        return CreateDeliveryResponse.from(savedDelivery);
    }

    //배송 경로 생성
    private List<DeliveryRoute> createDeliveryRoutes(Delivery savedDelivery, CreateDeliveryRequest request) {
        return request.getRoutes().stream()
                .map(route -> DeliveryRoute.create(
                        savedDelivery.getId(),
                        route.getSequence(),
                        route.getSourceHubId(),
                        route.getDestHubId(),
                        DEFAULT_ESTIMATED_DISTANCE,
                        DEFAULT_ESTIMATED_TIME
                ))
                .toList();
    }


    //배송 단건 조회 todo: 권한 별 기능 추가
    @Transactional(readOnly = true)
    public GetDeliveryResponse getDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송이 존재하지 않습니다."));

        return GetDeliveryResponse.from(delivery);
    }

    //배송 전체 목록 조회 todo: 권한 별 기능 추가
    @Transactional(readOnly = true)
    public List<GetDeliveryListResponse> getDeliveries() {
        return deliveryRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(GetDeliveryListResponse::from)
                .toList();
    }

    //출발 허브 기준 배송 목록 조회 todo: 권한 별 기능 추가
    @Transactional(readOnly = true)
    public List<GetDeliveryListResponse> getDeliveriesBySourceHub(UUID sourceHubId) {
        return deliveryRepository.findAllBySourceHubIdAndDeletedAtIsNull(sourceHubId)
                .stream()
                .map(GetDeliveryListResponse::from)
                .toList();
    }

    //도착 허브 기준 배송 목록 조회 todo: 권한 별 기능 추가
    @Transactional(readOnly = true)
    public List<GetDeliveryListResponse> getDeliveriesByDestHub(UUID destHubId) {
        return deliveryRepository.findAllByDestHubIdAndDeletedAtIsNull(destHubId)
                .stream()
                .map(GetDeliveryListResponse::from)
                .toList();
    }

    //배송 경로 조회
    @Transactional(readOnly = true)
    public List<GetDeliveryRouteResponse> getDeliveryRoutes(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송을 찾을 수 없습니다."));

        List<DeliveryRoute> routes = deliveryRouteRepository.findAllByDeliveryIdOrderBySequenceAsc(delivery.getId());

        return routes.stream()
                .map(GetDeliveryRouteResponse::from)
                .toList();
    }

    //배송 상태 수정 todo: 권한 별 기능 추가
    @Transactional
    public void updateDeliveryStatus(UUID deliveryId, DeliveryStatus deliveryStatus) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송이 존재하지 않습니다."));

        delivery.updateStatus(deliveryStatus);
    }

    //배송 경로 상태 수정 todo: 권한 별 기능 추가
    @Transactional
    public void updateDeliveryRouteStatus(UUID deliveryRouteId, DeliveryRouteStatus deliveryStatus) {
        DeliveryRoute deliveryRoute = deliveryRouteRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로가 존재하지 않습니다."));

        deliveryRoute.updateStatus(deliveryStatus);
    }

    //배송 삭제 todo: 권한 별 기능 추가
    @Transactional
    public void deleteDelivery(UUID deliveryId, UUID deletedBy) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송이 존재하지 않습니다."));

        delivery.delete(deletedBy);
    }
}