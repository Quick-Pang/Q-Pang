package com.qpang.hub.application.dto;

import com.qpang.hub.domain.model.HubType;
import java.math.BigDecimal;
import java.util.UUID;

public record HubUpdateCommand(
        String name,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        UUID managerId,
        HubType hubType,
        UUID centerHubId
) {}