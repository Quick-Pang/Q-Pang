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
        this.status = ProductStatus.OUT_OF_STOCK;
    }

    public void update(String name) {
        if (name == null || name.isBlank()) {
            throw new CustomException(CommonErrorCode.MISSING_INPUT_VALUE);
        }
        this.name = name;
    }
    public void changeStatus(ProductStatus newStatus) {
        if (newStatus == null) {
            throw new CustomException(CommonErrorCode.MISSING_INPUT_VALUE);
        }

        // 단종은 되돌릴 수 없음
        if (this.status == ProductStatus.DISCONTINUED) {
            throw new CustomException(ProductErrorCode.INVALID_STATUS_TRANSITION);
        }

        // 재고가 있는데 품절/단종으로 변경 불가
        if (this.stockQuantity > 0 &&
                (newStatus == ProductStatus.OUT_OF_STOCK || newStatus == ProductStatus.DISCONTINUED)) {
            throw new CustomException(ProductErrorCode.CANNOT_CHANGE_STATUS_WITH_STOCK);
        }

        // 재고가 없는데 판매중으로 변경 불가
        if (this.stockQuantity == 0 && newStatus == ProductStatus.AVAILABLE) {
            throw new CustomException(ProductErrorCode.CANNOT_CHANGE_STATUS_WITHOUT_STOCK);
        }

        this.status = newStatus;
    }

    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void decreaseStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new CustomException(ProductErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        this.stockQuantity -= quantity;

        // 재고가 0이 되면 자동으로 OUT_OF_STOCK으로 변경
        if (this.stockQuantity == 0) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }
    }
}