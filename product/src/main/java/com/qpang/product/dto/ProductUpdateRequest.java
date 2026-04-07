package com.qpang.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    @Size(max = 200, message = "상품명은 200자 이하입니다.")
    private String name;
}