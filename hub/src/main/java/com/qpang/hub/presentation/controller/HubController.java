package com.qpang.hub.presentation.controller;

import com.qpang.hub.application.service.HubService;
import com.qpang.hub.presentation.dto.HubCreateRequest;
import com.qpang.hub.presentation.dto.HubResponse;
import com.qpang.hub.presentation.dto.HubUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    @GetMapping
    public Page<HubResponse> getAllHubs(@PageableDefault(size = 10) Pageable pageable) {
        return hubService.getAllHubs(pageable)
                .map(HubResponse::from);
    }

    @PostMapping
    public HubResponse createHub(@RequestBody HubCreateRequest request) {
        return hubService.createHub(request);
    }

    @GetMapping("/{hubId}")
    public HubResponse getHubById(@PathVariable(name = "hubId") UUID hubId) {
        return hubService.getHubById(hubId);
    }

    @PatchMapping("/{hubId}")
    public HubResponse updateHub(
            @PathVariable(name = "hubId") UUID hubId,
            @RequestBody HubUpdateRequest request) {
        return hubService.updateHub(hubId, request);
    }

    @DeleteMapping("/{hubId}")
    public ResponseEntity<Void> deleteHub(@PathVariable(name = "hubId") UUID hubId) {
        hubService.deleteHub(hubId, 1L);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/init")
    public ResponseEntity<String> initHubData(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<HubCreateRequest> requests) {
        hubService.initHubData(userId, requests);
        return ResponseEntity.ok("총 " + requests.size() + "건의 허브 데이터 삽입이 완료되었습니다.");
    }
}