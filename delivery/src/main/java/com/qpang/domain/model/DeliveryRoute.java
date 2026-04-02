package com.qpang.domain.model;

import com.qpang.common.entity.BaseUserEntity;
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

    @Column(name = "delivery_status")
    private String deliveryStatus;

    @Column(name = "deleivery_manager")
    private UUID deliveryManager;

    // 생성자 (Lombok 대신 직접 유지)
    private DeliveryRoute(
            UUID deliveryId,
            Integer sequence,
            UUID sourceHubId,
            UUID destHubId
    ) {
        this.deliveryId = deliveryId;
        this.sequence = sequence;
        this.sourceHubId = sourceHubId;
        this.destHubId = destHubId;
        this.deliveryStatus = "WAITING";
    }

    public static DeliveryRoute create(
            UUID deliveryId,
            Integer sequence,
            UUID sourceHubId,
            UUID destHubId
    ) {
        return new DeliveryRoute(
                deliveryId,
                sequence,
                sourceHubId,
                destHubId
        );
    }
}
