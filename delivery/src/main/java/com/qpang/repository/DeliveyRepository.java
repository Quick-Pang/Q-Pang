package com.qpang.repository;

import com.qpang.domain.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveyRepository extends JpaRepository<Delivery, UUID> {
}
