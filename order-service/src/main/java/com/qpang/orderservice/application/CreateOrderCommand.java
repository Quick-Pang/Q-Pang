package com.qpang.orderservice.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.qpang.orderservice.domain.entity.Order;

public record CreateOrderCommand(
    UUID supplyCompanyId,
    UUID requestCompanyId,
    UUID userId,
    UUID deliveryId,
    Long price,
    LocalDateTime desiredArrival,
    String requestMemo,
    UUID createdBy,
    List<CreateOrderItemCommand> items
) {
    public CreateOrderCommand {
        items = items == null ? List.of() : List.copyOf(items);
    }

    public Order toOrder() {
        Order order = Order.create(
            supplyCompanyId,
            requestCompanyId,
            userId,
            deliveryId,
            price,
            desiredArrival,
            requestMemo,
            createdBy
        );
        for (CreateOrderItemCommand item : items) {
            order.addItem(item.productId(), item.quantity(), createdBy);
        }
        return order;
    }
}
