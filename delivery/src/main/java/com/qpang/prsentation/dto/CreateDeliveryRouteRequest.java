package com.qpang.prsentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateDeliveryRouteRequest {
    @NotNull
    private Integer sequence;

    @NotNull
    private UUID sourceHubId;

    @NotNull
    private UUID destHubId;
}
