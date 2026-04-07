package com.qpang.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetHubRouteRequest {
    private UUID sourceHubId;
    private UUID destHubId;
}