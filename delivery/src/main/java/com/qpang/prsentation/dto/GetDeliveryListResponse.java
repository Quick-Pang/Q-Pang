package com.qpang.prsentation.dto;

import com.qpang.domain.enums.DeliveryStatus;
import com.qpang.domain.model.Delivery;

import java.util.UUID;

public record GetDeliveryListResponse(
        UUID deliveryId,
        UUID orderId,
        UUID sourceHubId,
        UUID destHubId,
        String receiverName,
        String deliveryAddress,
        DeliveryStatus deliveryStatus
) {
    public static GetDeliveryListResponse from(Delivery delivery) {
        return new GetDeliveryListResponse(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getSourceHubId(),
                delivery.getDestHubId(),
                delivery.getReceiverName(),
                delivery.getDeliveryAddress(),
                delivery.getDeliveryStatus()
        );
    }
}