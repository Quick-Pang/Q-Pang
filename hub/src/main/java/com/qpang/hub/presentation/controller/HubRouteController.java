package com.qpang.hub.presentation.controller;

import com.qpang.hub.application.dto.HubRouteResponseDto;
import com.qpang.hub.application.service.HubRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hub-routes")
@RequiredArgsConstructor
public class HubRouteController {

    private final HubRouteService hubRouteService;

//    @GetMapping
//    public ResponseEntity<Page<HubRouteResponseDto>> getAllRoutes(Pageable pageable) {
//        return ResponseEntity.ok(hubRouteService.getAllRoutes(pageable));
//    }
//todo : 이영재 실험 작동요ㅕㅇ
    @GetMapping
    public ResponseEntity<Page<HubRouteResponseDto>> getAllRoutes(
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(hubRouteService.getAllRoutes(pageable));
    }

    @GetMapping("/path")
    public ResponseEntity<List<HubRouteResponseDto>> getPath(
            @RequestParam UUID sourceHubId,
            @RequestParam UUID destinationHubId
    ) {
        return ResponseEntity.ok(hubRouteService.getPath(sourceHubId, destinationHubId));
    }

}