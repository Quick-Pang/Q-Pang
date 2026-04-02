package com.qpang.application.service;

import com.qpang.domain.model.Delivery;
import com.qpang.repository.DeliveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveyRepository deliveyRepository;

    public Delivery saveDelivery(Delivery delivery) {
        return deliveyRepository.save(delivery);
    }
}
