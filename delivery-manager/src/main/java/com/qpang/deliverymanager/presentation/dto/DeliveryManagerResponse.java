package com.qpang.deliverymanager.presentation.dto;

import com.qpang.deliverymanager.application.dto.DeliveryManagerResult;
import com.qpang.deliverymanager.domain.model.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerResponse(
        UUID id,
        UUID userId,
        UUID hubId,
        String slackId,
        DeliveryManagerType managerType,
        Integer deliverySequence
) {
    public static DeliveryManagerResponse from(DeliveryManagerResult result) {
        return new DeliveryManagerResponse(
                result.id(),
                result.userId(),
                result.hubId(),
                result.slackId(),
                result.managerType(),
                result.deliverySequence()
        );
    }
}