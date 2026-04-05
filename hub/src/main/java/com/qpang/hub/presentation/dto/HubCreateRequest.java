package com.qpang.hub.presentation.dto;

import com.qpang.hub.application.dto.HubCreateCommand; // 추가
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
    public HubInitCommand toInitCommand() {
        return new HubInitCommand(name, address, latitude, longitude, managerId);
    }

    public HubCreateCommand toCreateCommand() {
        return new HubCreateCommand(name, address, latitude, longitude, managerId);
    }
}