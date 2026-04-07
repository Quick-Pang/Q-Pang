package com.qpang.prsentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class HubRouteInfo {

    private Integer sequence;
    private UUID sourceHubId;
    private UUID destHubId;
    private Double estimatedDistance;
    private Integer estimatedTime;
}