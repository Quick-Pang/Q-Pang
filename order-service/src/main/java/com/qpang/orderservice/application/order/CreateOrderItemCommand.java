package com.qpang.orderservice.application.order;

import java.util.UUID;

public record CreateOrderItemCommand(UUID productId, int quantity) {
    
}
