package com.qpang.prsentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateDeliveryRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID sourceHubId;

    @NotNull
    private UUID destHubId;

    @NotBlank
    private String deliveryAddress;

    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverSlackId;

    @Valid
    @NotNull
    private List<CreateDeliveryRouteRequest> routes;
}