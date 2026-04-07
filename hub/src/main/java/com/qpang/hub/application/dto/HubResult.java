package com.qpang.hub.application.dto;

import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.model.HubType;

import java.math.BigDecimal;
import java.util.UUID;

public record HubResult(
        UUID id,
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID managerId,
        HubType hubType,
        UUID centerHubId
) {
    public static HubResult from(Hub hub) {
        return new HubResult(
                hub.getId(),
                hub.getName(),
                hub.getAddress(),
                hub.getLatitude(),
                hub.getLongitude(),
                hub.getManagerId(),
                hub.getHubType(),
                hub.getCenterHub() != null ? hub.getCenterHub().getId() : null
        );
    }
}