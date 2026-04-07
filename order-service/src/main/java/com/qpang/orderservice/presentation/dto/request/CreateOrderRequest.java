package com.qpang.orderservice.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
    @NotNull UUID supplyCompanyId,
    @NotNull UUID requestCompanyId,
    @NotNull @Positive Long price,
    @NotNull LocalDateTime desiredArrival,
    String requestMemo,
    @NotNull @Size(min = 1) List<@Valid CreateOrderItemRequest> items
    ) {

}
