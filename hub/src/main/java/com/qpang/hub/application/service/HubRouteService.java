package com.qpang.hub.application.service;

import com.qpang.hub.application.dto.HubRouteResponseDto;
import com.qpang.hub.domain.repository.HubRepository;
import com.qpang.hub.domain.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
public class HubRouteService {

    private final HubRouteRepository hubRouteRepository;
    private final HubRepository hubRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "hubRouteList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<HubRouteResponseDto> getAllRoutes(Pageable pageable) {
        return hubRouteRepository.findAll(pageable)
                .map(HubRouteResponseDto::from);
    }
}