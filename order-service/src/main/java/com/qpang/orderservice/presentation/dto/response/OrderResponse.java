package com.qpang.orderservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.qpang.orderservice.domain.order.OrderStatus;
import com.qpang.orderservice.domain.order.entity.Order;

public record OrderResponse(
    UUID orderId, 
    UUID supplyCompanyId, 
    UUID requestCompanyId, 
    UUID userId,
    UUID deliveryId, 
    Long price, 
    OrderStatus status, 
    LocalDateTime desiredArrival, 
    String requestMemo, 
    LocalDateTime createdAt, 
    LocalDateTime updatedAt, 
    List<OrderItemResponse> items) {
    
    public static OrderResponse from(Order order){
        return new OrderResponse(
            order.getId(),
            order.getSupplyCompanyId(),
            order.getRequestCompanyId(),
            order.getUserId(),
            order.getDeliveryId(),
            order.getPrice(),
            order.getStatus(),
            order.getDesiredArrival(),
            order.getRequestMemo(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            order.getItems().stream().map(i -> new OrderItemResponse(i.getProductId(), i.getQuantity())).toList());
        
            
    }
}
