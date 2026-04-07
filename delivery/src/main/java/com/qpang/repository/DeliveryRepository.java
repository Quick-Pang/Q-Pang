package com.qpang.repository;

import com.qpang.domain.model.Delivery;
import com.qpang.domain.model.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByIdAndDeletedAtIsNull(UUID deliveryId);

    List<Delivery> findAllByDeletedAtIsNull();
    List<Delivery> findAllBySourceHubIdAndDeletedAtIsNull(UUID sourceHubId);
    List<Delivery> findAllByDestHubIdAndDeletedAtIsNull(UUID destHubId);
}
