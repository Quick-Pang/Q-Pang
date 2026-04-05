package com.qpang.hub.application.dto;

import com.qpang.hub.domain.model.HubRoute;

import java.math.BigDecimal;
import java.util.UUID;

public record HubRouteResponseDto(
        UUID id,
        UUID sourceHubId,
        UUID destinationHubId,
        Integer duration,
        BigDecimal distance
) {
    public static HubRouteResponseDto from(HubRoute route) {
        return new HubRouteResponseDto(
                route.getId(),
                route.getSourceHub().getId(),
                route.getDestinationHub().getId(),
                route.getDuration(),
                route.getDistance()
        );
    }
}