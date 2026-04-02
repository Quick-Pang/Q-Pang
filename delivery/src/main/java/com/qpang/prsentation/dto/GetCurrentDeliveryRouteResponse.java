package com.qpang.prsentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetCurrentDeliveryRouteResponse {

    private UUID deliveryRouteId;
    private Integer sequence;
    private UUID sourceHubId;
    private UUID destHubId;
    private String deliveryStatus;
}