package com.qpang.product.repository;

import com.qpang.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);
    List<Product> findAllByDeletedAtIsNull();

    List<Product> findAllByCompanyIdAndDeletedAtIsNull(UUID companyId);
}