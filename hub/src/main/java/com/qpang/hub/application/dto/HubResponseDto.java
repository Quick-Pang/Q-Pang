package com.qpang.hub.application.dto;

import com.qpang.hub.domain.model.Hub;

import java.math.BigDecimal;
import java.util.UUID;

public record HubResponseDto(
        UUID id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID managerId
) {
    public static HubResponseDto from(Hub entity) {
        return new HubResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getManagerId()
        );
    }
}