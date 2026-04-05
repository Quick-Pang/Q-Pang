package com.qpang.hub.application.service;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.hub.application.dto.HubCreateCommand;
import com.qpang.hub.application.dto.HubInitCommand;
import com.qpang.hub.application.dto.HubResponseDto;
import com.qpang.hub.application.dto.HubUpdateCommand;
import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;

    @Transactional(readOnly = true)
    public Page<HubResponseDto> getAllHubs(Pageable pageable) {
        return hubRepository.findAll(pageable)
                .map(HubResponseDto::from);
    }

    @Transactional
    public HubResponseDto createHub(Long userId, HubCreateCommand command) {
        Hub hub = Hub.builder()
                .name(command.name())
                .address(command.address())
                .latitude(command.latitude())
                .longitude(command.longitude())
                .managerId(command.managerId())
                .build();
        Hub savedHub = hubRepository.save(hub);
        return HubResponseDto.from(savedHub);
    }

    @Transactional(readOnly = true)
    public HubResponseDto getHubById(UUID hubId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));
        return HubResponseDto.from(hub);
    }

    @Transactional
    public HubResponseDto updateHub(UUID hubId, Long userId, HubUpdateCommand command) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

        hub.updateInfo(
                command.name(),
                command.address(),
                command.latitude(),
                command.longitude(),
                command.managerId()
        );

        return HubResponseDto.from(hub);
    }

    @Transactional
    public void deleteHub(UUID hubId, Long userId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

        hub.delete(userId);
    }

    @Transactional
    public void initHubData(Long userId, List<HubInitCommand> commands) {
        if (hubRepository.count() > 0) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        for (HubInitCommand cmd : commands) {
            Hub hub = Hub.builder()
                    .name(cmd.name())
                    .address(cmd.address())
                    .latitude(cmd.latitude())
                    .longitude(cmd.longitude())
                    .managerId(cmd.managerId())
                    .build();

            hubRepository.save(hub);
        }
    }
}