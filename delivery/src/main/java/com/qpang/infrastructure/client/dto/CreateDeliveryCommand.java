package com.qpang.infrastructure.client.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateDeliveryCommand {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID supplyCompanyId;

    @NotNull
    private UUID requestCompanyId;
}
