package com.qpang.orderservice.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
    UUID supplyCompanyId,
    UUID requestCompanyId,
    UUID userId,
    UUID deliveryId,
    Long price,
    LocalDateTime desiredArrival,
    String requestMemo,
    UUID createdBy,
    List<CreateOrderItemCommand> items){
        
    }
