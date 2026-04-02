package com.qpang.application.service;

import com.qpang.domain.model.Delivery;
import com.qpang.prsentation.dto.CreateDeliveryRequest;
import com.qpang.repository.DeliveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveyRepository deliveyRepository;

    public Delivery createDelivery(CreateDeliveryRequest request) {
        Delivery delivery = Delivery.create(
                request.getOrderId(),
                request.getSourceHubId(),
                request.getDestHubId(),
                request.getDeliveryAddress(),
                request.getReceiverName(),
                request.getReceiverSlackId()
        );

        return deliveyRepository.save(delivery);
    }
}
