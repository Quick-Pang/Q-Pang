package com.qpang.prsentation.dto;

import com.qpang.domain.enums.DeliveryRouteStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateDeliveryRouteStatusRequest {

    @NotNull
    private DeliveryRouteStatus deliveryStatus;
}