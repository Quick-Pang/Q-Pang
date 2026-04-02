package com.qpang.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank
    private String name;

    @NotNull
    private UUID companyId;

    @NotNull
    private UUID hubId;
}