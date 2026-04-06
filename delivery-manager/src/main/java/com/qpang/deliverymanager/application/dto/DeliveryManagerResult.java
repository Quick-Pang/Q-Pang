package com.qpang.deliverymanager.application.dto;

import com.qpang.deliverymanager.domain.model.DeliveryManager;
import com.qpang.deliverymanager.domain.model.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerResult(
        UUID id,
        UUID userId,
        UUID hubId,
        String slackId,
        DeliveryManagerType managerType,
        Integer deliverySequence
) {
    public static DeliveryManagerResult from(DeliveryManager deliveryManager) {
        return new DeliveryManagerResult(
                deliveryManager.getId(),
                deliveryManager.getUserId(),
                deliveryManager.getHubId(),
                deliveryManager.getSlackId(),
                deliveryManager.getManagerType(),
                deliveryManager.getDeliverySequence()
        );
    }
}