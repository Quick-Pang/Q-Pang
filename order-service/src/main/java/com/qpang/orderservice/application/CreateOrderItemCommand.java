package com.qpang.orderservice.application;

import java.util.UUID;

public record CreateOrderItemCommand(UUID productId, int quantity) {
    public CreateOrderItemCommand{
        if(productId == null){
            throw new IllegalArgumentException("상품ID는 비울 수 없습니다");
        }
        if(quantity <= 0){
            throw new IllegalArgumentException("수량은 0보다 커야야 합니다");
        }
    }
    
}
