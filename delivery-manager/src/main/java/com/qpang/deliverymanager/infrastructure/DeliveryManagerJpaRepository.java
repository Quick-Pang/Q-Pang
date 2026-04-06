package com.qpang.deliverymanager.infrastructure;

import com.qpang.deliverymanager.domain.model.DeliveryManager;
import com.qpang.deliverymanager.domain.model.DeliveryManagerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerJpaRepository extends JpaRepository<DeliveryManager, UUID> {

    @Override
    Page<DeliveryManager> findAll(Pageable pageable);

    Optional<DeliveryManager> findByUserId(UUID userId);

    @Query("""
            select max(d.deliverySequence)
            from DeliveryManager d
            where d.managerType = :managerType
            """)
    Integer findMaxSequenceByManagerType(DeliveryManagerType managerType);

    @Query("""
            select max(d.deliverySequence)
            from DeliveryManager d
            where d.hubId = :hubId and d.managerType = :managerType
            """)
    Integer findMaxSequenceByHubIdAndManagerType(UUID hubId, DeliveryManagerType managerType);
}