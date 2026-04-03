package com.qpang.hub.presentation.controller;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.hub.application.dto.HubInitCommand;
import com.qpang.hub.application.dto.HubResponseDto;
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
                .map(HubResponse::fromDto);
    }

    @PostMapping
    public HubResponse createHub(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody HubCreateRequest request) {
        HubResponseDto result = hubService.createHub(userId, request.toCreateCommand());
        return HubResponse.fromDto(result);
    }

    @GetMapping("/{hubId}")
    public HubResponse getHubById(@PathVariable(name = "hubId") UUID hubId) {
        HubResponseDto result = hubService.getHubById(hubId);
        return HubResponse.fromDto(result);
    }

    @PatchMapping("/{hubId}")
    public HubResponse updateHub(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable(name = "hubId") UUID hubId,
            @RequestBody HubUpdateRequest request) {
        HubResponseDto result = hubService.updateHub(hubId, userId, request.toCommand());
        return HubResponse.fromDto(result);
    }

    @DeleteMapping("/{hubId}")
    public ResponseEntity<Void> deleteHub(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable(name = "hubId") UUID hubId) {
        hubService.deleteHub(hubId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/init")
    public ResponseEntity<String> initHubData(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<HubCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        List<HubInitCommand> commands = requests.stream()
                .map(HubCreateRequest::toInitCommand)
                .toList();

        hubService.initHubData(userId, commands);
        return ResponseEntity.ok("데이터 삽입 완료");
    }
}