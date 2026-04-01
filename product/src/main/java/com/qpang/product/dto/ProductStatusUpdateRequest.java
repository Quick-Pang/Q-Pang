package com.qpang.product.dto;

import com.qpang.product.domain.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductStatusUpdateRequest {

    @NotNull
    private ProductStatus status;
}