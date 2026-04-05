package com.qpang.orderservice.presentation.dto.response;

import java.util.UUID;

public record OrderItemResponse(UUID productId, Integer quantity) {
    
}
