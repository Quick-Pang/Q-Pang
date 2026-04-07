package com.qpang.application.service;

import com.qpang.common.entity.UserRole;
import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.domain.enums.DeliveryRouteStatus;
import com.qpang.domain.enums.DeliveryStatus;
import com.qpang.domain.model.Delivery;
import com.qpang.domain.model.DeliveryRoute;
import com.qpang.exception.DeliveryErrorCode;
import com.qpang.infrastructure.client.CompanyServiceClient;
import com.qpang.infrastructure.client.HubServiceClient;
import com.qpang.infrastructure.client.dto.CompanyResponse;
import com.qpang.infrastructure.client.dto.CreateDeliveryCommand;
import com.qpang.infrastructure.client.dto.HubRouteResponse;
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
    private final HubServiceClient hubServiceClient;
    private final CompanyServiceClient companyServiceClient;
    private final SlackMessageService slackMessageService;

    @Transactional
    public CreateDeliveryResponse createDelivery(CreateDeliveryCommand command, UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);
        validateCreateDeliveryPermission(userRole);
        validateCreateDeliveryCommand(command);

        CompanyResponse supplyCompany = companyServiceClient
                .getCompany(command.getSupplyCompanyId())
                .getData();

        CompanyResponse requestCompany = companyServiceClient
                .getCompany(command.getRequestCompanyId())
                .getData();

        if (supplyCompany == null || supplyCompany.getHubId() == null) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_CREATE_INVALID_INPUT);
        }

        if (requestCompany == null || requestCompany.getHubId() == null) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_CREATE_INVALID_INPUT);
        }

        UUID sourceHubId = supplyCompany.getHubId();
        UUID destHubId = requestCompany.getHubId();

        List<HubRouteResponse> hubRoutes = hubServiceClient.getHubRoute(sourceHubId, destHubId);

        Delivery delivery = Delivery.create(
                command.getOrderId(),
                sourceHubId,
                destHubId,
                requestCompany.getAddress(),
                requestCompany.getName(),
                null
        );

        Delivery savedDelivery = deliveryRepository.save(delivery);

        List<DeliveryRoute> routeList = java.util.stream.IntStream.range(0, hubRoutes.size())
                .mapToObj(i -> {
                    HubRouteResponse route = hubRoutes.get(i);

                    return DeliveryRoute.create(
                            savedDelivery.getId(),
                            i + 1,
                            route.getSourceHubId(),
                            route.getDestinationHubId(),
                            route.getDistance() != null ? route.getDistance().doubleValue() : DEFAULT_ESTIMATED_DISTANCE,
                            route.getDuration() != null ? route.getDuration() : DEFAULT_ESTIMATED_TIME,
                            null
                    );
                })
                .toList();

        deliveryRouteRepository.saveAll(routeList);

        return CreateDeliveryResponse.from(savedDelivery);
    }

    private void sendSlackNotification(Delivery delivery) {
        String message = """
        배송 생성 알림

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
        request.setMessage(message);
        request.setSenderUserId(null);
        request.setRelatedType("DELIVERY");
        request.setRelatedId(delivery.getId());

        slackMessageService.createAndSendSlackMessage(request);
    }

    @Transactional(readOnly = true)
    public GetDeliveryResponse getDelivery(UUID deliveryId, UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);
        validateId(deliveryId);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        validateReadDeliveryPermission(delivery, userId, userRole);

        return GetDeliveryResponse.from(delivery);
    }

    @Transactional(readOnly = true)
    public List<GetDeliveryListResponse> getDeliveries(UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);

        if (isMaster(userRole) || isHubManager(userRole) || isSupplierManager(userRole)) {
            return deliveryRepository.findAllByDeletedAtIsNull()
                    .stream()
                    .map(GetDeliveryListResponse::from)
                    .toList();
        }

        if (isDeliveryManager(userRole)) {
            return deliveryRouteRepository.findAllByDeliveryManagerAndDeletedAtIsNull(userId)
                    .stream()
                    .map(route -> deliveryRepository.findByIdAndDeletedAtIsNull(route.getDeliveryId())
                            .orElse(null))
                    .filter(delivery -> delivery != null)
                    .distinct()
                    .map(GetDeliveryListResponse::from)
                    .toList();
        }

        throw new CustomException(CommonErrorCode.FORBIDDEN);
    }

    @Transactional(readOnly = true)
    public List<GetDeliveryListResponse> getDeliveriesBySourceHub(UUID sourceHubId, UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);
        validateId(sourceHubId);

        validateListPermission(userRole);

        return deliveryRepository.findAllBySourceHubIdAndDeletedAtIsNull(sourceHubId)
                .stream()
                .map(GetDeliveryListResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetDeliveryListResponse> getDeliveriesByDestHub(UUID destHubId, UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);
        validateId(destHubId);

        validateListPermission(userRole);

        return deliveryRepository.findAllByDestHubIdAndDeletedAtIsNull(destHubId)
                .stream()
                .map(GetDeliveryListResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetDeliveryRouteResponse> getDeliveryRoutes(UUID deliveryId, UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);
        validateId(deliveryId);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        validateReadDeliveryPermission(delivery, userId, userRole);

        List<DeliveryRoute> routes = deliveryRouteRepository.findAllByDeliveryIdOrderBySequenceAsc(delivery.getId());

        return routes.stream()
                .map(GetDeliveryRouteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public GetCurrentDeliveryRouteResponse getCurrentDeliveryRoute(UUID deliveryId, UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);
        validateId(deliveryId);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        validateReadDeliveryPermission(delivery, userId, userRole);

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

    @Transactional
    public void updateDelivery(UUID deliveryId, UpdateDeliveryRequest request, UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);
        validateId(deliveryId);
        validateUpdateDeliveryRequest(request);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        validateUpdateDeliveryPermission(delivery, userId, userRole);

        delivery.updateInfo(
                request.getDeliveryAddress(),
                request.getReceiverName(),
                request.getReceiverSlackId()
        );
    }

    @Transactional
    public void updateDeliveryStatus(UUID deliveryId, DeliveryStatus deliveryStatus, UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);
        validateId(deliveryId);

        if (deliveryStatus == null) {
            throw new CustomException(DeliveryErrorCode.INVALID_DELIVERY_STATUS);
        }

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        if (delivery.getDeletedAt() != null) {
            throw new CustomException(DeliveryErrorCode.DELIVERY_ALREADY_DELETED);
        }

        validateUpdateDeliveryPermission(delivery, userId, userRole);

        delivery.updateStatus(deliveryStatus);
    }

    @Transactional
    public void updateDeliveryRouteStatus(UUID deliveryRouteId, UpdateDeliveryRouteStatusRequest request, UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);
        validateId(deliveryRouteId);
        validateUpdateDeliveryRouteStatusRequest(request);

        DeliveryRoute deliveryRoute = deliveryRouteRepository.findByIdAndDeletedAtIsNull(deliveryRouteId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_ROUTE_NOT_FOUND));

        validateUpdateRoutePermission(deliveryRoute, userId, userRole);

        deliveryRoute.updateRouteProgress(
                request.getDeliveryStatus(),
                request.getActualDistance(),
                request.getActualTime()
        );

        updateDeliveryStatusByRoutes(deliveryRoute.getDeliveryId());
    }

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

    @Transactional
    public void deleteDelivery(UUID deliveryId, UUID userId, String userRole) {
        validateId(userId);
        validateRole(userRole);
        validateId(deliveryId);

        validateDeletePermission(userRole);

        Delivery delivery = deliveryRepository.findByIdAndDeletedAtIsNull(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        List<DeliveryRoute> routes =
                deliveryRouteRepository.findAllByDeliveryIdAndDeletedAtIsNullOrderBySequenceAsc(deliveryId);

        for (DeliveryRoute route : routes) {
            route.delete(userId);
        }

        delivery.delete(userId);
    }

    private void validateCreateDeliveryCommand(CreateDeliveryCommand command) {
        if (command == null ||
                command.getOrderId() == null ||
                command.getSupplyCompanyId() == null ||
                command.getRequestCompanyId() == null
        ) {
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

    private void validateRole(String userRole) {
        if (userRole == null || userRole.isBlank()) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        }

        try {
            UserRole.valueOf(userRole);
        } catch (IllegalArgumentException e) {
            throw new CustomException(CommonErrorCode.FORBIDDEN);
        }
    }

    private void validateCreateDeliveryPermission(String userRole) {
        if (isMaster(userRole) || isHubManager(userRole) || isSupplierManager(userRole)) {
            return;
        }
        throw new CustomException(CommonErrorCode.FORBIDDEN);
    }

    private void validateListPermission(String userRole) {
        if (isMaster(userRole) || isHubManager(userRole) || isSupplierManager(userRole)) {
            return;
        }
        throw new CustomException(CommonErrorCode.FORBIDDEN);
    }

    private void validateDeletePermission(String userRole) {
        if (isMaster(userRole)) {
            return;
        }
        throw new CustomException(CommonErrorCode.FORBIDDEN);
    }

    private void validateReadDeliveryPermission(Delivery delivery, UUID userId, String userRole) {
        if (isMaster(userRole) || isHubManager(userRole) || isSupplierManager(userRole)) {
            return;
        }

        if (isDeliveryManager(userRole)) {
            boolean isMyRoute = deliveryRouteRepository
                    .findAllByDeliveryIdAndDeletedAtIsNullOrderBySequenceAsc(delivery.getId())
                    .stream()
                    .anyMatch(route -> userId.equals(route.getDeliveryManager()));

            if (isMyRoute) {
                return;
            }
        }

        throw new CustomException(CommonErrorCode.FORBIDDEN);
    }

    private void validateUpdateDeliveryPermission(Delivery delivery, UUID userId, String userRole) {
        if (isMaster(userRole) || isHubManager(userRole)) {
            return;
        }

        if (isDeliveryManager(userRole)) {
            boolean isMyRoute = deliveryRouteRepository
                    .findAllByDeliveryIdAndDeletedAtIsNullOrderBySequenceAsc(delivery.getId())
                    .stream()
                    .anyMatch(route -> userId.equals(route.getDeliveryManager()));

            if (isMyRoute) {
                return;
            }
        }

        throw new CustomException(CommonErrorCode.FORBIDDEN);
    }

    private void validateUpdateRoutePermission(DeliveryRoute deliveryRoute, UUID userId, String userRole) {
        if (isMaster(userRole) || isHubManager(userRole)) {
            return;
        }

        if (isDeliveryManager(userRole) && userId.equals(deliveryRoute.getDeliveryManager())) {
            return;
        }

        throw new CustomException(CommonErrorCode.FORBIDDEN);
    }

    private boolean isMaster(String userRole) {
        return UserRole.MASTER.name().equals(userRole);
    }

    private boolean isHubManager(String userRole) {
        return UserRole.HUB_MANAGER.name().equals(userRole);
    }

    private boolean isDeliveryManager(String userRole) {
        return UserRole.DELIVERY_MANAGER.name().equals(userRole);
    }

    private boolean isSupplierManager(String userRole) {
        return UserRole.SUPPLIER_MANAGER.name().equals(userRole);
    }
}