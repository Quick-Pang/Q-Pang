package com.qpang.prsentation.dto;

import com.qpang.domain.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateDeliveryStatusRequest {

    @NotNull
    private DeliveryStatus deliveryStatus;
}