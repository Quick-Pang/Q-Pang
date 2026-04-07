package com.qpang.hub.presentation.controller;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.hub.application.dto.HubInitCommand;
import com.qpang.hub.application.dto.HubResult;
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
    public ResponseEntity<HubResponse> createHub(@RequestBody HubCreateRequest request) {
        HubResult result = hubService.createHub(request.toCommand());
        return ResponseEntity.ok(HubResponse.from(result));
    }

    @GetMapping("/{hubId}")
    public HubResponse getHubById(@PathVariable(name = "hubId") UUID hubId) {
        return HubResponse.from(hubService.getHubById(hubId));
    }

    @PatchMapping("/{hubId}")
    public HubResponse updateHub(
            @PathVariable(name = "hubId") UUID hubId,
            @RequestBody HubUpdateRequest request
    ) {
        return HubResponse.from(hubService.updateHub(hubId, request.toCommand()));
    }

    @DeleteMapping("/{hubId}")
    public ResponseEntity<Void> deleteHub(@PathVariable(name = "hubId") UUID hubId) {
        hubService.deleteHub(hubId,  UUID.fromString("11111111-1111-1111-1111-111111111111"));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/init")
    public ResponseEntity<String> initHubData(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<HubCreateRequest> requests
    ) {
        if (requests == null || requests.isEmpty()) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        List<HubInitCommand> commands = requests.stream()
                .map(req -> new HubInitCommand(
                        req.name(),
                        req.address(),
                        req.latitude(),
                        req.longitude(),
                        req.managerId(),
                        req.hubType(),
                        req.centerHubId()
                ))
                .toList();

        hubService.initHubData(userId, commands);
        return ResponseEntity.ok("데이터 삽입 완료");
    }
}