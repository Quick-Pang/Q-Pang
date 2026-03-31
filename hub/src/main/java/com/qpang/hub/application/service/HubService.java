package com.qpang.hub.application.service;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.repository.HubRepository;
import com.qpang.hub.presentation.dto.HubCreateRequest;
import com.qpang.hub.presentation.dto.HubResponse;
import com.qpang.hub.presentation.dto.HubUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;

    @Transactional(readOnly = true)
    public Page<Hub> getAllHubs(Pageable pageable) {
        return hubRepository.findAll(pageable);
    }

    @Transactional
    public HubResponse createHub(HubCreateRequest request) {
        Hub hub = Hub.builder()
                .name(request.name())
                .address(request.address())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .managerId(request.managerId())
                .build();

        Hub savedHub = hubRepository.save(hub);
        return HubResponse.from(savedHub);
    }

    @Transactional(readOnly = true)
    public HubResponse getHubById(UUID hubId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

        return HubResponse.from(hub);
    }

    @Transactional
    public HubResponse updateHub(UUID hubId, HubUpdateRequest request) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));
        hub.updateInfo(
                request.name(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.managerId()
        );

        return HubResponse.from(hub);
    }

    @Transactional
    public void deleteHub(UUID hubId, Long userId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));
        hub.delete(userId);
    }
}