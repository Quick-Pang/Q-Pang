package com.qpang.orderservice.presentation.dto.request;

import com.qpang.orderservice.domain.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record ChangeOrderStatusRequest(@NotNull OrderStatus status) {
    
}
