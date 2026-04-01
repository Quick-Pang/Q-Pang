package com.qpang.product.domain.entity;

import com.qpang.common.entity.BaseUserEntity;
import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.product.domain.enums.ProductStatus;
import com.qpang.product.exception.ProductErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_product")
@NoArgsConstructor
public class Product extends BaseUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_name", nullable = false, length = 200)
    private String name;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "hub_id", nullable = false)
    private UUID hubId;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false)
    private ProductStatus status;

    @Builder
    public Product(String name, UUID companyId, UUID hubId) {
        this.name = name;
        this.companyId = companyId;
        this.hubId = hubId;
        this.stockQuantity = 0;
        this.status = ProductStatus.AVAILABLE;
    }

    public void update(String name) {
        if (name == null || name.isBlank()) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        this.name = name;
    }

    public void changeStatus(ProductStatus status) {
        if (status == null) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        this.status = status;
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void decreaseStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new CustomException(ProductErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        this.stockQuantity -= quantity;
    }
}