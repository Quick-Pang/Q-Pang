package com.qpang.hub.presentation.dto;

import com.qpang.hub.application.dto.HubResponseDto;

import java.math.BigDecimal;
import java.util.UUID;

public record HubResponse(
        UUID id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID managerId
) {
    public static HubResponse fromDto(HubResponseDto dto) {
        return new HubResponse(
                dto.id(),
                dto.name(),
                dto.address(),
                dto.latitude(),
                dto.longitude(),
                dto.managerId()
        );
    }
}