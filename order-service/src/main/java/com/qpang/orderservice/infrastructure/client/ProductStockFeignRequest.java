package com.qpang.orderservice.infrastructure.client;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductStockFeignRequest( @NotNull @Min(1) Integer quantity) {
    
}
