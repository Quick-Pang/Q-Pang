package com.qpang.product.dto;

import com.qpang.product.domain.entity.Product;
import com.qpang.product.domain.enums.ProductStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ProductResponse {

    private UUID id;
    private String name;
    private UUID companyId;
    private UUID hubId;
    private int stockQuantity;
    private ProductStatus status;

    private ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.companyId = product.getCompanyId();
        this.hubId = product.getHubId();
        this.stockQuantity = product.getStockQuantity();
        this.status = product.getStatus();
    }

    public static ProductResponse from(Product product) {
        return new ProductResponse(product);
    }
}