package com.qpang.hub.domain.model;

import com.qpang.common.entity.BaseUserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_hub")
@Where(clause = "deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Hub extends BaseUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private BigDecimal latitude;

    @Column(nullable = false)
    private BigDecimal longitude;

    @Column(name = "manager_id")
    private UUID managerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "hub_type", nullable = false)
    private HubType hubType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_hub_id")
    private Hub centerHub;

    public void updateInfo(
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            UUID managerId,
            HubType hubType,
            Hub centerHub
    ) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
        if (latitude != null) this.latitude = latitude;
        if (longitude != null) this.longitude = longitude;
        if (managerId != null) this.managerId = managerId;
        if (hubType != null) this.hubType = hubType;
        if (centerHub != null) this.centerHub = centerHub;
    }
}