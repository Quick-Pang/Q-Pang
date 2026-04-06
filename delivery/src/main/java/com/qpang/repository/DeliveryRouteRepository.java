package com.qpang.repository;

import com.qpang.domain.model.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID> {

    List<DeliveryRoute> findAllByDeliveryIdOrderBySequenceAsc(UUID deliveryId);
    List<DeliveryRoute> findAllByDeliveryIdAndDeletedAtIsNullOrderBySequenceAsc(UUID deliveryId);
    Optional<DeliveryRoute> findByIdAndDeletedAtIsNull(UUID deliveryRouteId);
}