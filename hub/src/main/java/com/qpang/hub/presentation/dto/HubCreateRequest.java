package com.qpang.hub.presentation.dto;

import com.qpang.hub.application.dto.HubCreateCommand;
import com.qpang.hub.domain.model.HubType;

import java.math.BigDecimal;
import java.util.UUID;

public record HubCreateRequest(
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID managerId,
        HubType hubType,
        UUID centerHubId
) {
    public HubCreateCommand toCommand() {
        return new HubCreateCommand(
                this.name,
                this.address,
                this.latitude,
                this.longitude,
                this.managerId,
                this.hubType,
                this.centerHubId
        );
    }
}