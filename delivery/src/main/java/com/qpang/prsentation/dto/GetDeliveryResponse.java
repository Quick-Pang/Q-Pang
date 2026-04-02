package com.qpang.prsentation.dto;

import com.qpang.domain.model.Delivery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GetDeliveryResponse {

    private final UUID deliveryId;
    private final UUID orderId;
    private final UUID sourceHubId;
    private final UUID destHubId;
    private final String deliveryAddress;
    private final String receiverName;
    private final String receiverSlackId;
    private final String deliveryStatus;

    private GetDeliveryResponse(
            UUID deliveryId,
            UUID orderId,
            UUID sourceHubId,
            UUID destHubId,
            String deliveryAddress,
            String receiverName,
            String receiverSlackId,
            String deliveryStatus
    ) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.sourceHubId = sourceHubId;
        this.destHubId = destHubId;
        this.deliveryAddress = deliveryAddress;
        this.receiverName = receiverName;
        this.receiverSlackId = receiverSlackId;
        this.deliveryStatus = deliveryStatus;
    }

    public static GetDeliveryResponse from(Delivery delivery) {
        return new GetDeliveryResponse(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getSourceHubId(),
                delivery.getDestHubId(),
                delivery.getDeliveryAddress(),
                delivery.getReceiverName(),
                delivery.getReceiverSlackId(),
                delivery.getDeliveryStatus().name()
        );
    }
}