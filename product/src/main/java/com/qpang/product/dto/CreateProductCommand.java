package com.qpang.product.dto;

import com.qpang.product.domain.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CreateProductCommand {

    private String name;
    private UUID companyId;
    private UUID hubId;

    public Product toEntity() {
        return Product.builder()
                .name(name)
                .companyId(companyId)
                .hubId(hubId)
                .build();
    }
}