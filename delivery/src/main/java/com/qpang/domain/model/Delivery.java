package com.qpang.domain.model;

import com.qpang.common.entity.BaseUserEntity;
import com.qpang.domain.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_delivery")
@Getter
@NoArgsConstructor
public class Delivery extends BaseUserEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "source_hub_id")
    private UUID sourceHubId;

    @Column(name = "dest_hub_id")
    private UUID destHubId;

    @Column(name = "delviery_address")
    private String deliveryAddress;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_slack_id")
    private String receiverSlackId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryStatus deliveryStatus = DeliveryStatus.WAITING_AT_HUB;

    private Delivery(
            UUID orderId,
            UUID sourceHubId,
            UUID destHubId,
            String deliveryAddress,
            String receiverName,
            String receiverSlackId
    ) {
        this.orderId = orderId;
        this.sourceHubId = sourceHubId;
        this.destHubId = destHubId;
        this.deliveryAddress = deliveryAddress;
        this.receiverName = receiverName;
        this.receiverSlackId = receiverSlackId;
        this.deliveryStatus = DeliveryStatus.WAITING_AT_HUB;
    }

    public static Delivery create(
            UUID orderId,
            UUID sourceHubId,
            UUID destHubId,
            String deliveryAddress,
            String receiverName,
            String receiverSlackId
    ) {
        return new Delivery(
                orderId,
                sourceHubId,
                destHubId,
                deliveryAddress,
                receiverName,
                receiverSlackId
        );

    }
}
