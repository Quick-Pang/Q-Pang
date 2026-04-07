package com.qpang.prsentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateDeliveryRequest {

    @NotBlank
    private String deliveryAddress;

    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverSlackId;
}