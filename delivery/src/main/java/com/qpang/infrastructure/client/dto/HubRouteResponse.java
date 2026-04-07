package com.qpang.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class HubRouteResponse {

    private UUID hubRouteId;
    private UUID sourceHubId;
    private UUID destinationHubId;
    private String sourceHubName;
    private String destinationHubName;
    private BigDecimal distance;
    private Integer duration;
}