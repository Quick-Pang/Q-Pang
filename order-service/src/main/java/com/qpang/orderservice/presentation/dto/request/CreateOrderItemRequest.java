package com.qpang.orderservice.presentation.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;



public record CreateOrderItemRequest(@NotNull UUID productId,@NotNull @Min(1) int quantity) {
    
    
}
