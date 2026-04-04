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
//todo: not null은 모든 객체 / notblank는 String 전용이고 빈문자열 공백까지 막음