package com.qpang.hub.domain.model;

import com.qpang.common.entity.BaseUserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "p_hub_routes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hub_route_source_destination",
                        columnNames = {"source_hub_id", "destination_hub_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class HubRoute extends BaseUserEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "route_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_hub_id", nullable = false)
    private Hub sourceHub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_hub_id", nullable = false)
    private Hub destinationHub;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private BigDecimal distance;

    public void updateRoute(Integer duration, BigDecimal distance) {
        this.duration = duration;
        this.distance = distance;
    }
}