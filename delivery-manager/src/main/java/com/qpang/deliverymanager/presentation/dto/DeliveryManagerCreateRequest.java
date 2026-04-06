package com.qpang.deliverymanager.presentation.dto;

import com.qpang.deliverymanager.application.dto.DeliveryManagerCreateCommand;
import com.qpang.deliverymanager.domain.model.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerCreateRequest(
        UUID userId,
        UUID hubId,
        String slackId,
        DeliveryManagerType managerType
) {
    public DeliveryManagerCreateCommand toCommand() {
        return new DeliveryManagerCreateCommand(
                this.userId,
                this.hubId,
                this.slackId,
                this.managerType
        );
    }
}