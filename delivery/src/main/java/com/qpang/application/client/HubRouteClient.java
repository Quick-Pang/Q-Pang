package com.qpang.application.client;

import com.qpang.prsentation.dto.HubRouteInfo;

import java.util.List;
import java.util.UUID;

public interface HubRouteClient {
    List<HubRouteInfo> getRoutes(UUID sourceHubId, UUID destHubId);
}