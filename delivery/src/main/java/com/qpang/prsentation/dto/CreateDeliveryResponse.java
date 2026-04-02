package com.qpang.prsentation.dto;

import com.qpang.domain.enums.DeliveryStatus;
import com.qpang.domain.model.Delivery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateDeliveryResponse {
    private UUID deliveryId;
    private DeliveryStatus deliveryStatus;

    public CreateDeliveryResponse(UUID deliveryId, DeliveryStatus deliveryStatus) {
        this.deliveryId = deliveryId;
        this.deliveryStatus = deliveryStatus;
    }

    public static CreateDeliveryResponse from(Delivery delivery) {
        return new CreateDeliveryResponse(
                delivery.getId(),
                delivery.getDeliveryStatus()
        );
    }
}
