package com.qpang.hub.application.dto;

import java.util.List;

public record HubPageCache(
        List<HubResult> content,
        long totalElements
) {
}
