package com.qpang.application.service;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.domain.enums.DeliveryRouteStatus;
import com.qpang.domain.enums.DeliveryStatus;
import com.qpang.domain.model.Delivery;
import com.qpang.domain.model.DeliveryRoute;
import com.qpang.exception.DeliveryErrorCode;
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
    //경로 번호, 거리, 시간 후에 주문 생성 시 orderService에서 값을 받아서 들어감

    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteRepository deliveryRouteRepository;

    // 배송 생성 todo: 서비스는 비즈니스 규칙 검증, 엔티티가 도메인 규칙 검증, dto가 입력값 형식 검증
    @Transactional
    public CreateDeliveryResponse createDelivery(CreateDeliveryRequest request) {
        validateCreateDeliveryRequest(request);

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
        // todo from이 객체를 받아서 다른 객체로 변환할 때 쓰는 메서드명 관례고 그냥 savedDelivery 보내면 안되는 건
        // 다른 데이터들 값도 들어가 있음createdAt updatedAt deletedAt 같은것
    }

    // 배송 경로 생성
    //todo 배송 경로를 여러개 만들어 줄거야 배송 객체와 dto를 통해 딜리버리루트라는 객체 리스트를 만들어 반환
    private List<DeliveryRoute> createDeliveryRoutes(Delivery savedDelivery, CreateDeliveryRequest request) {
        return request.getRoutes().stream()
                .map(route -> {
                    if (route.getSourceHubId() == null || route.getDestHubId() == null) {
                        throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_ROUTE_INPUT);
                    }

                    return DeliveryRoute.create(
                            savedDelivery.getId(),
                            route.getSequence(),
                            route.getSourceHubId(),
                            route.getDestHubId(),
                            DEFAULT_ESTIMATED_DISTANCE,
                            DEFAULT_ESTIMATED_TIME
                    );
                })
                .toList();
    }

    // 배송 단건 조회
    @Transactional(readOnly = true)
    //todo“이 메서드는 조회용이다”라는 의미가 분명해짐
    //JPA/Hibernate가 약간 더 최적화할 수 있음
    //실수로 수정성 로직이 들어가는 걸 막는 데 도움됨
    public GetDeliveryResponse getDelivery(UUID deliveryId) {
        validateId(deliveryId);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                //소프트 딜리트 구별
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        return GetDeliveryResponse.from(delivery);
    }

    // 배송 전체 목록 조회
    @Transactional(readOnly = true)
    public List<GetDeliveryListResponse> getDeliveries() {
        return deliveryRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(GetDeliveryListResponse::from)
                .toList();
    }

    // 출발 허브 기준 배송 목록 조회
    @Transactional(readOnly = true)
    public List<GetDeliveryListResponse> getDeliveriesBySourceHub(UUID sourceHubId) {
        validateId(sourceHubId);

        return deliveryRepository.findAllBySourceHubIdAndDeletedAtIsNull(sourceHubId)
                .stream()
                .map(GetDeliveryListResponse::from)
                .toList();
    }

    // 도착 허브 기준 배송 목록 조회
    @Transactional(readOnly = true)
    public List<GetDeliveryListResponse> getDeliveriesByDestHub(UUID destHubId) {
        validateId(destHubId);

        return deliveryRepository.findAllByDestHubIdAndDeletedAtIsNull(destHubId)
                .stream()
                .map(GetDeliveryListResponse::from)
                .toList();
    }

    // 배송 경로 조회 배송 ID로
    @Transactional(readOnly = true)
    public List<GetDeliveryRouteResponse> getDeliveryRoutes(UUID deliveryId) {
        validateId(deliveryId);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        List<DeliveryRoute> routes = deliveryRouteRepository.findAllByDeliveryIdOrderBySequenceAsc(delivery.getId());

        return routes.stream()
                .map(GetDeliveryRouteResponse::from)
                .toList();
    }

    // 현재 진행 경로 확인
    @Transactional(readOnly = true)
    public GetCurrentDeliveryRouteResponse getCurrentDeliveryRoute(UUID deliveryId) {
        validateId(deliveryId);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        List<DeliveryRoute> routes = deliveryRouteRepository
                .findAllByDeliveryIdAndDeletedAtIsNullOrderBySequenceAsc(delivery.getId());

        DeliveryRoute currentRoute = routes.stream()
                .filter(route -> route.getDeliveryStatus() != DeliveryRouteStatus.DELIVERED)
                .findFirst()
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.CURRENT_DELIVERY_ROUTE_NOT_FOUND));

        return new GetCurrentDeliveryRouteResponse(
                currentRoute.getId(),
                currentRoute.getSequence(),
                currentRoute.getSourceHubId(),
                currentRoute.getDestHubId(),
                currentRoute.getDeliveryStatus().name()
        );
    }

    // 배송 정보 수정
    @Transactional
    public void updateDelivery(UUID deliveryId, UpdateDeliveryRequest request) {
        validateId(deliveryId);
        validateUpdateDeliveryRequest(request);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        delivery.updateInfo(
                request.getDeliveryAddress(),
                request.getReceiverName(),
                request.getReceiverSlackId()
        );
    }

    // 배송 상태 수정 todo: 포함어있지 않은 상태 입력시 오류
    @Transactional
    public void updateDeliveryStatus(UUID deliveryId, DeliveryStatus deliveryStatus) {
        validateId(deliveryId);

        if (deliveryStatus == null) {
            throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_STATUS);
        }

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        if (delivery.getDeletedAt() != null) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_ALREADY_DELETED);
        }

        delivery.updateStatus(deliveryStatus);
    }

    // 배송 경로 상태 수정
    @Transactional
    public void updateDeliveryRouteStatus(UUID deliveryRouteId, UpdateDeliveryRouteStatusRequest request) {
        validateId(deliveryRouteId);
        validateUpdateDeliveryRouteStatusRequest(request);

        DeliveryRoute deliveryRoute = deliveryRouteRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_ROUTE_NOT_FOUND));

        if (deliveryRoute.getDeletedAt() != null) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_ROUTE_ALREADY_DELETED);
        }

        deliveryRoute.updateRouteProgress(
                request.getDeliveryStatus(),
                request.getActualDistance(),
                request.getActualTime()
        );
    }

    // 배송 삭제
    @Transactional
    public void deleteDelivery(UUID deliveryId, UUID deletedBy) {
        validateId(deliveryId);
        validateId(deletedBy);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        if (delivery.getDeletedAt() != null) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_ALREADY_DELETED);
        }

        delivery.delete(deletedBy);
    }

    private void validateCreateDeliveryRequest(CreateDeliveryRequest request) {
        if (request == null ||
                request.getOrderId() == null ||
                request.getSourceHubId() == null ||
                request.getDestHubId() == null ||
                request.getDeliveryAddress() == null || request.getDeliveryAddress().isBlank() ||
                request.getReceiverName() == null || request.getReceiverName().isBlank() ||
                request.getRoutes() == null || request.getRoutes().isEmpty()) {
            throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_INPUT);
        } //todo 조건 중 하나라도 true면 예외
    }

    private void validateUpdateDeliveryRequest(UpdateDeliveryRequest request) {
        if (request == null ||
                request.getDeliveryAddress() == null || request.getDeliveryAddress().isBlank() ||
                request.getReceiverName() == null || request.getReceiverName().isBlank()) {
            throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_INPUT);
        }
    }

    private void validateUpdateDeliveryRouteStatusRequest(UpdateDeliveryRouteStatusRequest request) {
        if (request == null || request.getDeliveryStatus() == null) {
            throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_ROUTE_STATUS);
        }

        if (request.getActualDistance() < 0 || request.getActualTime() < 0) {
            throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_ROUTE_INPUT);
        }
    }

    private void validateId(UUID id) {
        if (id == null) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }
}