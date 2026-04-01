package com.qpang.hub.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HubUpdateRequest(
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID managerId
) {}