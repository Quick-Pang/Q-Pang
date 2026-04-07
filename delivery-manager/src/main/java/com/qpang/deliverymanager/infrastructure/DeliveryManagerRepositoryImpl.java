package com.qpang.deliverymanager.infrastructure;

import com.qpang.deliverymanager.domain.model.DeliveryManager;
import com.qpang.deliverymanager.domain.model.DeliveryManagerType;
import com.qpang.deliverymanager.domain.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryManagerRepositoryImpl implements DeliveryManagerRepository {

    private final DeliveryManagerJpaRepository deliveryManagerJpaRepository;

    @Override
    public DeliveryManager save(DeliveryManager deliveryManager) {
        return deliveryManagerJpaRepository.save(deliveryManager);
    }

    @Override
    public Optional<DeliveryManager> findById(UUID id) {
        return deliveryManagerJpaRepository.findById(id);
    }

    @Override
    public Optional<DeliveryManager> findByUserId(UUID userId) {
        return deliveryManagerJpaRepository.findByUserId(userId);
    }

    @Override
    public Page<DeliveryManager> findAll(Pageable pageable) {
        return deliveryManagerJpaRepository.findAll(pageable);
    }

    @Override
    public Integer findMaxSequenceByManagerType(DeliveryManagerType managerType) {
        return deliveryManagerJpaRepository.findMaxSequenceByManagerType(managerType);
    }

    @Override
    public Integer findMaxSequenceByHubIdAndManagerType(UUID hubId, DeliveryManagerType managerType) {
        return deliveryManagerJpaRepository.findMaxSequenceByHubIdAndManagerType(hubId, managerType);
    }
}