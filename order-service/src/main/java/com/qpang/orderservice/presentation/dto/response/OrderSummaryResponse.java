package com.qpang.orderservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.qpang.orderservice.domain.OrderStatus;

public record OrderSummaryResponse(
    UUID orderId, 
    OrderStatus status, 
    Long price, 
    LocalDateTime createdAt) {
    
}
