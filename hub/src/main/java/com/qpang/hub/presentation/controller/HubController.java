package com.qpang.hub.presentation.controller;

import com.qpang.hub.application.service.HubService;
import com.qpang.hub.presentation.dto.HubResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    /**
     * 전체 허브 목록 조회 (페이징 적용)
     * @param pageable 페이지 번호, 사이즈, 정렬 기준
     * @return 페이징 처리된 HubResponse DTO 리스트
     */
    @GetMapping
    public Page<HubResponse> getAllHubs(@PageableDefault(size = 10) Pageable pageable) {
        return hubService.getAllHubs(pageable)
                .map(HubResponse::from);
    }
}