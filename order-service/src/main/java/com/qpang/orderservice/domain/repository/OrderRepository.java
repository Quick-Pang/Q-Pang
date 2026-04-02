package com.qpang.orderservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.qpang.orderservice.domain.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID>{

    Optional<Order> findByIdAndDeletedAtIsNull(UUID id);

    Page<Order> findAllByDeletedAtIsNull(Pageable pageable);
}
