package com.qpang.infrastructure.client;

import com.qpang.infrastructure.client.dto.GetHubRouteRequest;
import com.qpang.infrastructure.client.dto.GetDeliveryInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "hub-service")
public interface HubServiceClient {

    @PostMapping("/hub-routes/search")
    GetDeliveryInfoResponse getHubRoute(@RequestBody GetHubRouteRequest request);
}