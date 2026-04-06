package com.qpang.hub.application.service;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.hub.application.dto.HubCreateCommand;
import com.qpang.hub.application.dto.HubInitCommand;
import com.qpang.hub.application.dto.HubResult;
import com.qpang.hub.application.dto.HubUpdateCommand;
import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.model.HubType;
import com.qpang.hub.domain.repository.HubRepository;
import com.qpang.hub.domain.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;
    private final HubRouteManager hubRouteManager;
    private final HubRouteRepository hubRouteRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "hubList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<HubResult> getAllHubs(Pageable pageable) {
        return hubRepository.findAll(pageable)
                .map(HubResult::from);
    }

    @Transactional
    @CacheEvict(value = {"hubList", "hubRouteList"}, allEntries = true)
    public HubResult createHub(HubCreateCommand command) {

        Hub centerHub = null;

        if (command.hubType() == HubType.NORMAL) {
            centerHub = hubRepository.findById(command.centerHubId())
                    .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

            if (centerHub.getHubType() != HubType.CENTER) {
                throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
            }
        }

        Hub hub = Hub.builder()
                .name(command.name())
                .address(command.address())
                .latitude(command.latitude())
                .longitude(command.longitude())
                .managerId(command.managerId())
                .hubType(command.hubType())
                .centerHub(centerHub)
                .build();

        Hub savedHub = hubRepository.save(hub);

        if (savedHub.getHubType() == HubType.CENTER) {
            hubRouteManager.reassignRoutesForNewCenter(savedHub);
        } else {
            hubRouteManager.setupRouteForNewNormal(savedHub);
        }

        return HubResult.from(savedHub);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "hubs", key = "#hubId")
    public HubResult getHubById(UUID hubId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));
        return HubResult.from(hub);
    }

    @Transactional
    @CachePut(value = "hubs", key = "#hubId")
    @CacheEvict(value = {"hubList", "hubRouteList"}, allEntries = true)
    public HubResult updateHub(UUID hubId, HubUpdateCommand command) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

        Hub centerHub = null;

        if (command.hubType() == HubType.NORMAL) {
            centerHub = hubRepository.findById(command.centerHubId())
                    .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

            if (centerHub.getHubType() != HubType.CENTER) {
                throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
            }
        }

        hub.updateInfo(
                command.name(),
                command.address(),
                command.latitude(),
                command.longitude(),
                command.managerId(),
                command.hubType(),
                centerHub
        );

        return HubResult.from(hub);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "hubs", key = "#hubId"),
            @CacheEvict(value = {"hubList", "hubRouteList"}, allEntries = true)
    })
    public void deleteHub(UUID hubId, UUID userId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

        if (hub.getHubType() == HubType.CENTER) {
            List<Hub> children = hubRepository.findAllByCenterHub(hub).stream()
                    .filter(child -> !child.getId().equals(hub.getId()))
                    .toList();

            if (!children.isEmpty()) {
                throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
            }
        }

        hubRouteRepository.deleteByHub(hub);
        hub.delete(userId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @CacheEvict(value = {"hubs", "hubList", "hubRouteList"}, allEntries = true)
    public void initHubData(UUID userId, List<HubInitCommand> commands) {
        if (hubRepository.count() > 0) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        List<Hub> savedHubs = new ArrayList<>();

        for (HubInitCommand cmd : commands) {
            Hub hub = Hub.builder()
                    .name(cmd.name())
                    .address(cmd.address())
                    .latitude(cmd.latitude())
                    .longitude(cmd.longitude())
                    .managerId(cmd.managerId())
                    .hubType(cmd.hubType())
                    .build();

            savedHubs.add(hubRepository.save(hub));
        }

        for (int i = 0; i < savedHubs.size(); i++) {
            Hub savedHub = savedHubs.get(i);
            HubInitCommand cmd = commands.get(i);

            if (cmd.hubType() == HubType.CENTER) {
                continue;
            }

            Hub centerHub = hubRepository.findById(cmd.centerHubId())
                    .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

            if (centerHub.getHubType() != HubType.CENTER) {
                throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
            }

            savedHub.updateInfo(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    centerHub
            );
        }

        for (Hub hub : savedHubs) {
            if (hub.getHubType() == HubType.NORMAL) {
                hubRouteManager.setupRouteForNewNormal(hub);
            }
        }
    }
}