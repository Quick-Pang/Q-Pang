package com.qpang.hub.application.dto;

import java.util.List;

public record HubRoutePageCache(
        List<HubRouteResponseDto> content,
        long totalElements
) {
}
