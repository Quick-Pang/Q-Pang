package com.qpang.domain.model;

import com.qpang.common.entity.BaseUserEntity;
import com.qpang.domain.enums.DeliveryRouteStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_route")
@Getter
@NoArgsConstructor
public class DeliveryRoute extends BaseUserEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "source_hub_id")
    private UUID sourceHubId;

    @Column(name = "dest_hub_id")
    private UUID destHubId;

    @Column(name = "estimated_distance")
    private Double estimatedDistance;

    @Column(name = "estimated_time")
    private Integer estimatedTime;

    @Column(name = "actual_distance")
    private Double actualDistance;

    @Column(name = "actual_time")
    private Integer actualTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryRouteStatus deliveryStatus;

    @Column(name = "deleivery_manager")
    private UUID deliveryManager;

    private DeliveryRoute(
            UUID deliveryId,
            Integer sequence,
            UUID sourceHubId,
            UUID destHubId,
            Double estimatedDistance,
            Integer estimatedTime
    ) {
        this.deliveryId = deliveryId;
        this.sequence = sequence;
        this.sourceHubId = sourceHubId;
        this.destHubId = destHubId;
        this.estimatedDistance = estimatedDistance;
        this.estimatedTime = estimatedTime;
        this.deliveryStatus = DeliveryRouteStatus.WAITING_AT_HUB;
    }

    public static DeliveryRoute create(
            UUID deliveryId,
            Integer sequence,
            UUID sourceHubId,
            UUID destHubId,
            Double estimatedDistance,
            Integer estimatedTime,
            UUID deliveryManager
    ) {
        return new DeliveryRoute(
                deliveryId,
                sequence,
                sourceHubId,
                destHubId,
                estimatedDistance,
                estimatedTime
        );
    }

    public void updateStatus(DeliveryRouteStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public void updateRouteProgress(DeliveryRouteStatus deliveryStatus, Double actualDistance, Integer actualTime) {
        this.deliveryStatus = deliveryStatus;
        this.actualDistance = actualDistance;
        this.actualTime = actualTime;
    }
}