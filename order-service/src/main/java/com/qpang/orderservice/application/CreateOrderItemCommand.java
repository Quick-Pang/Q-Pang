package com.qpang.orderservice.application;

import java.util.UUID;

import com.qpang.common.exception.CustomException;
import com.qpang.orderservice.exception.OrderErrorCode;

public record CreateOrderItemCommand(UUID productId, int quantity) {
    public CreateOrderItemCommand{
        if(productId == null || quantity <= 0){
            throw new CustomException(OrderErrorCode.ORDER_ITEM_INVALID);
        }
    }
    
}
