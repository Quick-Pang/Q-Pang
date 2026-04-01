package com.qpang.product.repository;

import com.qpang.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByDeletedAtIsNull();

    List<Product> findAllByCompanyIdAndDeletedAtIsNull(UUID companyId);
}