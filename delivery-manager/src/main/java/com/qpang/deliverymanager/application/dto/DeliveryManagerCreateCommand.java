package com.qpang.deliverymanager.application.dto;

import com.qpang.deliverymanager.domain.model.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerCreateCommand(
        UUID userId,
        UUID hubId,
        String slackId,
        DeliveryManagerType managerType
) {
}