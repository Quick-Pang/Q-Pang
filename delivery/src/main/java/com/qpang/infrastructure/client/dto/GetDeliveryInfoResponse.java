package com.qpang.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class GetDeliveryInfoResponse {

    private UUID destHubId;
    private List<RouteItem> routes;

    @Getter
    @NoArgsConstructor
    public static class RouteItem {
        private Integer sequence;
        private UUID sourceHubId;
        private UUID destHubId;
        private Double estimatedDistance;
        private Integer estimatedTime;
        private UUID deliveryManager;
    }
}