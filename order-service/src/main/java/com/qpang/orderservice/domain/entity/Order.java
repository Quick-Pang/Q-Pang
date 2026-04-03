package com.qpang.orderservice.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.qpang.common.entity.BaseEntity;
import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.orderservice.domain.OrderStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity{


    @Column(name = "id", nullable = false, updatable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "supply_company_id", nullable = false)
    private UUID supplyCompanyId;

    @Column(name = "request_company_id", nullable = false)
    private UUID requestCompanyId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    //BIGINT
    @Column(name = "price", nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "desired_arrival", nullable = false)
    private LocalDateTime desiredArrival;

    @Column(name = "request_memo", columnDefinition = "text")
    private String requestMemo;
    
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderItem> items = new ArrayList<>();

    @Builder
    private Order(UUID supplyCompanyId, UUID requestCompanyId, UUID userId, UUID deliveryId, Long price, LocalDateTime desiredArrival, String requestMemo, UUID createdBy){
        this.supplyCompanyId = supplyCompanyId;
        this.requestCompanyId = requestCompanyId;
        this.userId = userId;
        this.deliveryId = deliveryId;
        this.price = price;
        this.desiredArrival = desiredArrival;
        this.requestMemo = requestMemo;
        this.createdBy = createdBy;
        this.status = OrderStatus.CREATED;
    }

    public static Order create(UUID supplyCompanyId, UUID requestCompanyId, UUID userId, UUID deliveryId, Long price, LocalDateTime desiredArrival, String requestMemo, UUID createdBy) {
            return Order.builder()
                    .supplyCompanyId(supplyCompanyId)
                    .requestCompanyId(requestCompanyId)
                    .userId(userId)
                    .deliveryId(deliveryId)
                    .price(price)
                    .desiredArrival(desiredArrival)
                    .requestMemo(requestMemo)
                    .createdBy(createdBy)
                    .build();
    }

    public void addItem(UUID productId, int quantity, UUID itemCreatedBy) {
        OrderItem item = OrderItem.create(this, productId, quantity, itemCreatedBy);
        this.items.add(item);
    }
    public void changeStatus(OrderStatus newStatus) {
        if (deletedAt != null) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        if (status == OrderStatus.CANCELLED) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        if (status == OrderStatus.CREATED && newStatus == OrderStatus.CANCELLED) {
            this.status = newStatus;
            return;
        }
        throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
    }
    public void softDelete(UUID deletedBy) {
        if (deletedAt != null) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        this.deletedBy = deletedBy;
        this.deletedAt = LocalDateTime.now();
    }
}
