package com.qpang.deliverymanager.presentation.dto;

import com.qpang.deliverymanager.application.dto.DeliveryManagerUpdateCommand;
import com.qpang.deliverymanager.domain.model.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerUpdateRequest(
        UUID hubId,
        String slackId,
        DeliveryManagerType managerType
) {
    public DeliveryManagerUpdateCommand toCommand() {
        return new DeliveryManagerUpdateCommand(
                this.hubId,
                this.slackId,
                this.managerType
        );
    }
}