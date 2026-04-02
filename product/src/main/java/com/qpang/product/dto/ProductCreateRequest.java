package com.qpang.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotNull
    private UUID companyId;

    @NotNull
    private UUID hubId;
}