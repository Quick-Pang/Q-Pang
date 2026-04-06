package com.qpang.deliverymanager.domain.model;

import com.qpang.common.entity.BaseUserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_manager")
@Where(clause = "deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DeliveryManager extends BaseUserEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "slack_id", nullable = false, length = 100)
    private String slackId;

    @Enumerated(EnumType.STRING)
    @Column(name = "manager_type", nullable = false, length = 30)
    private DeliveryManagerType managerType;

    @Column(name = "delivery_sequence", nullable = false)
    private Integer deliverySequence;

    public static DeliveryManager create(
            UUID userId,
            UUID hubId,
            String slackId,
            DeliveryManagerType managerType,
            Integer deliverySequence
    ) {
        validate(userId, hubId, slackId, managerType, deliverySequence);

        return DeliveryManager.builder()
                .id(userId)
                .userId(userId)
                .hubId(hubId)
                .slackId(slackId)
                .managerType(managerType)
                .deliverySequence(deliverySequence)
                .build();
    }

    public void update(
            UUID hubId,
            String slackId,
            DeliveryManagerType managerType
    ) {
        validate(this.userId, hubId, slackId, managerType, this.deliverySequence);

        this.hubId = hubId;
        this.slackId = slackId;
        this.managerType = managerType;
    }

    private static void validate(
            UUID userId,
            UUID hubId,
            String slackId,
            DeliveryManagerType managerType,
            Integer deliverySequence
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
        if (slackId == null || slackId.isBlank()) {
            throw new IllegalArgumentException("slackId는 필수입니다.");
        }
        if (managerType == null) {
            throw new IllegalArgumentException("managerType은 필수입니다.");
        }
        if (deliverySequence == null || deliverySequence < 0) {
            throw new IllegalArgumentException("deliverySequence는 0 이상이어야 합니다.");
        }

        if (managerType == DeliveryManagerType.COMPANY && hubId == null) {
            throw new IllegalArgumentException("업체 배송 담당자는 hubId가 필수입니다.");
        }
    }
}