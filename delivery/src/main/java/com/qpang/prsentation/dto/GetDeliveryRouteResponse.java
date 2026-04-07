package com.qpang.prsentation.dto;

import com.qpang.domain.model.DeliveryRoute;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GetDeliveryRouteResponse {

    private final UUID deliveryRouteId;
    private final Integer sequence;
    private final UUID sourceHubId;
    private final UUID destHubId;
    private final Double estimatedDistance;
    private final Integer estimatedTime;
    private final String deliveryStatus;

    private GetDeliveryRouteResponse(
            UUID deliveryRouteId,
            Integer sequence,
            UUID sourceHubId,
            UUID destHubId,
            Double estimatedDistance,
            Integer estimatedTime,
            String deliveryStatus
    ) {
        this.deliveryRouteId = deliveryRouteId;
        this.sequence = sequence;
        this.sourceHubId = sourceHubId;
        this.destHubId = destHubId;
        this.estimatedDistance = estimatedDistance;
        this.estimatedTime = estimatedTime;
        this.deliveryStatus = deliveryStatus;
    }

    public static GetDeliveryRouteResponse from(DeliveryRoute deliveryRoute) {
        return new GetDeliveryRouteResponse(
                deliveryRoute.getId(),
                deliveryRoute.getSequence(),
                deliveryRoute.getSourceHubId(),
                deliveryRoute.getDestHubId(),
                deliveryRoute.getEstimatedDistance(),
                deliveryRoute.getEstimatedTime(),
                deliveryRoute.getDeliveryStatus().name()
        );
    }
}