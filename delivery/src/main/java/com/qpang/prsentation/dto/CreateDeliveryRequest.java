package com.qpang.prsentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Getter
public class CreateDeliveryRequest {

    @NotNull(message = "orderId는 필수입니다.")
    private UUID orderId;

    @NotNull(message = "sourceHubId는 필수입니다.")
    private UUID sourceHubId;

    @NotNull(message = "destHubId는 필수입니다.")
    private UUID destHubId;

    @NotBlank(message = "deliveryAddress는 필수입니다.")
    private String deliveryAddress;

    @NotBlank(message = "receiverName은 필수입니다.")
    private String receiverName;

    @NotBlank(message = "receiverSlackId는 필수입니다.")
    private String receiverSlackId;
}
