package com.qpang.application.service;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.domain.enums.DeliveryRouteStatus;
import com.qpang.domain.enums.DeliveryStatus;
import com.qpang.domain.model.Delivery;
import com.qpang.domain.model.DeliveryRoute;
import com.qpang.exception.DeliveryErrorCode;
import com.qpang.infrastructure.client.HubServiceClient;
import com.qpang.infrastructure.client.dto.CreateDeliveryCommand;
import com.qpang.infrastructure.client.dto.GetHubRouteRequest;
import com.qpang.infrastructure.client.dto.GetDeliveryInfoResponse;
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
    private final HubServiceClient hubServiceClient;
    private final SlackMessageService slackMessageService;

    // 배송 생성 todo: 출발 허브 담당 도메인 정하기, 허브 도메인에게 이동 경로 받아오기 연동할기
    @Transactional
    public CreateDeliveryResponse createDelivery(CreateDeliveryCommand command) {
        validateCreateDeliveryCommand(command);

        GetDeliveryInfoResponse deliveryInfo = hubServiceClient.getHubRoute(
                new GetHubRouteRequest(
                        command.getSourceHubId(),
                        command.getDeliveryAddress()
                )
        );

        Delivery delivery = Delivery.create(
                command.getOrderId(),
                command.getSourceHubId(),
                deliveryInfo.getDestHubId(),
                command.getDeliveryAddress(),
                command.getReceiverName(),
                command.getReceiverSlackId()
        );

        Delivery savedDelivery = deliveryRepository.save(delivery);

        List<DeliveryRoute> routeList = deliveryInfo.getRoutes().stream()
                .map(route -> DeliveryRoute.create(
                        savedDelivery.getId(),
                        route.getSequence(),
                        route.getSourceHubId(),
                        route.getDestHubId(),
                        route.getEstimatedDistance(),
                        route.getEstimatedTime(),
                        route.getDeliveryManager()
                ))
                .toList();

        deliveryRouteRepository.saveAll(routeList);

        try {
            sendSlackNotification(savedDelivery);
        } catch (Exception e) {
            System.out.println("슬랙 알림 전송 실패: " + e.getMessage());
        }

        return CreateDeliveryResponse.from(savedDelivery);
    }

    private void sendSlackNotification(Delivery delivery) {
        String message = """
        🚚 배송 생성 알림

        주문 ID: %s
        출발 허브: %s
        도착 허브: %s
        수령인: %s
        주소: %s
        """.formatted(
                delivery.getOrderId(),
                delivery.getSourceHubId(),
                delivery.getDestHubId(),
                delivery.getReceiverName(),
                delivery.getDeliveryAddress()
        );

        CreateSlackMessageRequest request = new CreateSlackMessageRequest();
//        request.setReceiverSlackId(); //todo: 허브 담당자에게 슬랙으로 바꾸기
        request.setMessage(message);
        request.setSenderUserId(null);
        request.setRelatedType("DELIVERY");
        request.setRelatedId(delivery.getId());

        slackMessageService.createAndSendSlackMessage(request);
    }

    // 배송 단건 조회
    @Transactional(readOnly = true)
    public GetDeliveryResponse getDelivery(UUID deliveryId) {
        validateId(deliveryId);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
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

    // 배송 경로 조회
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

//    // 배송 경로 상태 수정
    @Transactional
    public void updateDeliveryRouteStatus(UUID deliveryRouteId, UpdateDeliveryRouteStatusRequest request) {
        validateId(deliveryRouteId);
        validateUpdateDeliveryRouteStatusRequest(request);

        DeliveryRoute deliveryRoute = deliveryRouteRepository.findByIdAndDeletedAtIsNull(deliveryRouteId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_ROUTE_NOT_FOUND));

        deliveryRoute.updateRouteProgress(
                request.getDeliveryStatus(),
                request.getActualDistance(),
                request.getActualTime()
        );

        updateDeliveryStatusByRoutes(deliveryRoute.getDeliveryId());
    }

    //전체 배송 경로 상태 자동 수정
    private void updateDeliveryStatusByRoutes(UUID deliveryId) {
        List<DeliveryRoute> routes =
                deliveryRouteRepository.findAllByDeliveryIdAndDeletedAtIsNullOrderBySequenceAsc(deliveryId);

        if (routes.isEmpty()) {
            return;
        }

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        boolean allWaiting = routes.stream()
                .allMatch(route -> route.getDeliveryStatus() == DeliveryRouteStatus.WAITING_AT_HUB);

        boolean allDelivered = routes.stream()
                .allMatch(route -> route.getDeliveryStatus() == DeliveryRouteStatus.DELIVERED);

        boolean hasInProgress = routes.stream()
                .anyMatch(route ->
                        route.getDeliveryStatus() == DeliveryRouteStatus.IN_TRANSIT_HUB ||
                        route.getDeliveryStatus() == DeliveryRouteStatus.ARRIVED_AT_DEST_HUB ||
                        route.getDeliveryStatus() == DeliveryRouteStatus.OUT_FOR_DELIVERY
                );

        if (allDelivered) {
            delivery.updateStatus(DeliveryStatus.COMPLETED);
            return;
        }

        if (hasInProgress) {
            delivery.updateStatus(DeliveryStatus.IN_PROGRESS);
            return;
        }

        if (allWaiting) {
            delivery.updateStatus(DeliveryStatus.WAITING);
        }
    }

    // 배송 삭제
    @Transactional
    public void deleteDelivery(UUID deliveryId, UUID deletedBy) {
        validateId(deliveryId);
        validateId(deletedBy);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        List<DeliveryRoute> routes =
                deliveryRouteRepository.findAllByDeliveryIdAndDeletedAtIsNullOrderBySequenceAsc(deliveryId);

        for (DeliveryRoute route : routes) {
            route.delete(deletedBy);
        }

        delivery.delete(deletedBy);
    }

    private void validateCreateDeliveryCommand(CreateDeliveryCommand command) {
        if (command == null ||
                command.getOrderId() == null ||
                command.getSourceHubId() == null ||
                command.getDeliveryAddress() == null || command.getDeliveryAddress().trim().isEmpty() ||
                command.getReceiverName() == null || command.getReceiverName().trim().isEmpty()) {
            throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_INPUT);
        }
    }

    private void validateUpdateDeliveryRequest(UpdateDeliveryRequest request) {
        if (request == null ||
                request.getDeliveryAddress() == null || request.getDeliveryAddress().trim().isEmpty() ||
                request.getReceiverName() == null || request.getReceiverName().trim().isEmpty()) {
            throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_INPUT);
        }
    }

    private void validateUpdateDeliveryRouteStatusRequest(UpdateDeliveryRouteStatusRequest request) {
        if (request == null || request.getDeliveryStatus() == null) {
            throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_ROUTE_STATUS);
        }

        if (request.getActualDistance() == null || request.getActualTime() == null) {
            throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_ROUTE_INPUT);
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