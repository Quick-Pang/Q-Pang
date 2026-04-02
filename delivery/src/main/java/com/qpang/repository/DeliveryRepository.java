package com.qpang.repository;

import com.qpang.domain.model.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<DeliveryRoute, UUID> {
}
