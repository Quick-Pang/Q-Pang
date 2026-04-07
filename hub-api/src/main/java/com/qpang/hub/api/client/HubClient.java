package com.qpang.hub.api.client;

import com.qpang.hub.api.dto.HubResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service", path = "/api/hubs")
public interface HubClient {

    @GetMapping("/{hubId}")
    HubResponse getHubById(@PathVariable("hubId") UUID hubId);
}
