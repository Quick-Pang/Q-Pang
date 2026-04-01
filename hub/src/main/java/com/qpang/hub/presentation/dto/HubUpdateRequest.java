package com.qpang.hub.presentation.dto;

import com.qpang.hub.application.dto.HubUpdateCommand;
import java.math.BigDecimal;
import java.util.UUID;

public record HubUpdateRequest(
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID managerId
) {
    public HubUpdateCommand toCommand() {
        return new HubUpdateCommand(name, address, latitude, longitude, managerId);
    }
}