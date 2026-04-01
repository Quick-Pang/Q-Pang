package com.qpang.hub.presentation.dto;

import com.qpang.hub.application.dto.HubInitCommand;
import java.math.BigDecimal;
import java.util.UUID;

public record HubCreateRequest(
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID managerId
) {
    public HubInitCommand toCommand() {
        return new HubInitCommand(
                this.name,
                this.address,
                this.latitude,
                this.longitude,
                this.managerId
        );
    }
}