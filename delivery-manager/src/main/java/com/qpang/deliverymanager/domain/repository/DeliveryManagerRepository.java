package com.qpang.deliverymanager.domain.repository;

import com.qpang.deliverymanager.domain.model.DeliveryManager;
import com.qpang.deliverymanager.domain.model.DeliveryManagerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository {

    DeliveryManager save(DeliveryManager deliveryManager);

    Optional<DeliveryManager> findById(UUID id);

    Optional<DeliveryManager> findByUserId(UUID userId);

    Page<DeliveryManager> findAll(Pageable pageable);

    Integer findMaxSequenceByManagerType(DeliveryManagerType managerType);

    Integer findMaxSequenceByHubIdAndManagerType(UUID hubId, DeliveryManagerType managerType);
}