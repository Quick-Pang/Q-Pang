package com.qpang.prsentation.dto;

import com.qpang.domain.model.Delivery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateDeliveryResponse {

    private UUID deliveryId;
    private UUID orderId;
    private String deliveryStatus;

    public static CreateDeliveryResponse from(Delivery delivery) {
        return new CreateDeliveryResponse(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getDeliveryStatus().name()
        );
    }
}