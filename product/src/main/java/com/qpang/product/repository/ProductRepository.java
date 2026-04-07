package com.qpang.product.repository;

import com.qpang.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByDeletedAtIsNull();

    List<Product> findAllByCompanyIdAndDeletedAtIsNull(UUID companyId);

    Page<Product> findByNameContainingAndDeletedAtIsNull(String name, Pageable pageable);

    Page<Product> findByCompanyIdAndNameContainingAndDeletedAtIsNull(UUID companyId, String name, Pageable pageable);
}