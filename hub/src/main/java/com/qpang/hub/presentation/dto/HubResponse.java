package com.qpang.hub.presentation.dto;

import com.qpang.hub.application.dto.HubResult;
import com.qpang.hub.domain.model.HubType;

import java.math.BigDecimal;
import java.util.UUID;

public record HubResponse(
        UUID id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID managerId,
        HubType hubType,
        UUID centerHubId
) {
    public static HubResponse from(HubResult result) {
        return new HubResponse(
                result.id(),
                result.name(),
                result.address(),
                result.latitude(),
                result.longitude(),
                result.managerId(),
                result.hubType(),
                result.centerHubId()
        );
    }
}