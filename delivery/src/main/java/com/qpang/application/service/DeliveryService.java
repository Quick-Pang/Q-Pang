package com.qpang.application.service;

import com.qpang.domain.model.Delivery;
import com.qpang.domain.model.DeliveryRoute;
import com.qpang.prsentation.dto.CreateDeliveryRequest;
import com.qpang.prsentation.dto.CreateDeliveryResponse;
import com.qpang.repository.DeliveryRepository;
import com.qpang.repository.DeliveryRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private static final int FIRST_ROUTE_SEQUENCE = 1;
    private static final double DEFAULT_ESTIMATED_DISTANCE = 0.0;
    private static final int DEFAULT_ESTIMATED_TIME = 0;

    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteRepository deliveryRouteRepository;

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

    private List<DeliveryRoute> createDeliveryRoutes(Delivery savedDelivery, CreateDeliveryRequest request) {

        DeliveryRoute firstRoute = DeliveryRoute.create(
                savedDelivery.getId(),
                FIRST_ROUTE_SEQUENCE,
                request.getSourceHubId(),
                request.getDestHubId(),
                DEFAULT_ESTIMATED_DISTANCE,
                DEFAULT_ESTIMATED_TIME
        );

        return List.of(firstRoute);
    }
}