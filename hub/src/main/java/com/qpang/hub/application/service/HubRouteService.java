package com.qpang.hub.application.service;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.hub.application.dto.HubRoutePageCache;
import com.qpang.hub.application.dto.HubRouteResponseDto;
import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.model.HubRoute;
import com.qpang.hub.domain.model.HubType;
import com.qpang.hub.domain.repository.HubRepository;
import com.qpang.hub.domain.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageImpl;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubRouteService {

    private final HubRouteRepository hubRouteRepository;
    private final HubRepository hubRepository;

    @Transactional(readOnly = true)
    public Page<HubRouteResponseDto> getAllRoutes(Pageable pageable) {
        HubRoutePageCache cachedPage = getCachedHubRoutePage(pageable);
        return new PageImpl<>(cachedPage.content(), pageable, cachedPage.totalElements());
    }

    @Cacheable(
            value = "hubRoutePageList",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()"
    )
    @Transactional(readOnly = true)
    public HubRoutePageCache getCachedHubRoutePage(Pageable pageable) {
        Page<HubRouteResponseDto> routes = hubRouteRepository.findAll(pageable)
                .map(HubRouteResponseDto::from);
        return new HubRoutePageCache(routes.getContent(), routes.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<HubRouteResponseDto> getPath(UUID sourceHubId, UUID destinationHubId) {
        Hub sourceHub = hubRepository.findById(sourceHubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

        Hub destinationHub = hubRepository.findById(destinationHubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

        List<Hub> hubPath = buildHubPath(sourceHub, destinationHub);

        List<HubRouteResponseDto> result = new ArrayList<>();

        for (int i = 0; i < hubPath.size() - 1; i++) {
            Hub current = hubPath.get(i);
            Hub next = hubPath.get(i + 1);

            HubRoute route = hubRouteRepository.findBySourceHubAndDestinationHub(current, next)
                    .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

            result.add(HubRouteResponseDto.from(route));
        }

        return result;
    }

    private List<Hub> buildHubPath(Hub sourceHub, Hub destinationHub) {
        List<Hub> path = new ArrayList<>();

        if (sourceHub.getId().equals(destinationHub.getId())) {
            path.add(sourceHub);
            return path;
        }

        Hub sourceCenter = getCenterHub(sourceHub);
        Hub destinationCenter = getCenterHub(destinationHub);

        path.add(sourceHub);

        if (!sourceHub.getId().equals(sourceCenter.getId())) {
            path.add(sourceCenter);
        }

        if (!sourceCenter.getId().equals(destinationCenter.getId())) {
            path.add(destinationCenter);
        }

        if (!destinationHub.getId().equals(destinationCenter.getId())) {
            path.add(destinationHub);
        }

        return path;
    }

    private Hub getCenterHub(Hub hub) {

        if (hub.getHubType() == HubType.CENTER) {
            return hub;
        }

        Hub center = hub.getCenterHub();
        if (center == null) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        if (center.getHubType() != HubType.CENTER) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        return center;
    }
}
