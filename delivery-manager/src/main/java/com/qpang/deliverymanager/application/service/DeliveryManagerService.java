package com.qpang.deliverymanager.application.service;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.deliverymanager.application.dto.DeliveryManagerCreateCommand;
import com.qpang.deliverymanager.application.dto.DeliveryManagerResult;
import com.qpang.deliverymanager.application.dto.DeliveryManagerUpdateCommand;
import com.qpang.deliverymanager.domain.model.DeliveryManager;
import com.qpang.deliverymanager.domain.model.DeliveryManagerType;
import com.qpang.deliverymanager.domain.repository.DeliveryManagerRepository;
import com.qpang.hub.api.client.HubClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final HubClient hubClient;

    @Transactional(readOnly = true)
    public Page<DeliveryManagerResult> getAll(Pageable pageable) {
        return deliveryManagerRepository.findAll(pageable)
                .map(DeliveryManagerResult::from);
    }

    @Transactional
    public DeliveryManagerResult create(DeliveryManagerCreateCommand command) {
        validateHubIfNeeded(command.hubId(), command.managerType());

        if (deliveryManagerRepository.findByUserId(command.userId()).isPresent()) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        int nextSequence = getNextSequence(command.hubId(), command.managerType());

        DeliveryManager deliveryManager = DeliveryManager.create(
                command.userId(),
                command.hubId(),
                command.slackId(),
                command.managerType(),
                nextSequence
        );

        DeliveryManager saved = deliveryManagerRepository.save(deliveryManager);
        return DeliveryManagerResult.from(saved);
    }

    @Transactional(readOnly = true)
    public DeliveryManagerResult getById(UUID id) {
        DeliveryManager deliveryManager = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

        return DeliveryManagerResult.from(deliveryManager);
    }

    @Transactional
    public DeliveryManagerResult update(UUID id, DeliveryManagerUpdateCommand command) {
        DeliveryManager deliveryManager = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

        validateHubIfNeeded(command.hubId(), command.managerType());

        deliveryManager.update(
                command.hubId(),
                command.slackId(),
                command.managerType()
        );

        return DeliveryManagerResult.from(deliveryManager);
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        DeliveryManager deliveryManager = deliveryManagerRepository.findById(id)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));

        deliveryManager.delete(userId);
    }

    private void validateHubIfNeeded(UUID hubId, DeliveryManagerType managerType) {
        if (managerType == DeliveryManagerType.COMPANY) {
            if (hubId == null) {
                throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
            }

            try {
                hubClient.getHubById(hubId);
            } catch (FeignException.NotFound e) {
                throw new CustomException(CommonErrorCode.NOT_FOUND);
            }
        }
    }

    private int getNextSequence(UUID hubId, DeliveryManagerType managerType) {
        Integer maxSequence = managerType == DeliveryManagerType.HUB
                ? deliveryManagerRepository.findMaxSequenceByManagerType(managerType)
                : deliveryManagerRepository.findMaxSequenceByHubIdAndManagerType(hubId, managerType);

        return maxSequence == null ? 0 : maxSequence + 1;
    }
}
