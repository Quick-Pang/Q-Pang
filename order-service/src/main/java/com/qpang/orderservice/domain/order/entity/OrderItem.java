package com.qpang.orderservice.domain.order.entity;

import java.util.UUID;

import com.qpang.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Entity
@Table(name = "p_order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity{
    @Column(name = "id", nullable = false, updatable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    static OrderItem create(Order order, UUID productId, Integer quantity, UUID createdBy) {
        if(quantity <= 0) {
            throw new IllegalArgumentException("");
        }
        OrderItem item = new OrderItem();
        item.order = order;
        item.productId = productId;
        item.quantity = quantity;
        item.createdBy = createdBy;
        return item;
    }
}