package com.qpang.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CompanyUpdateRequest {

    @NotBlank(message = "업체명은 필수 입력값입니다.")
    @Size(max = 200, message = "업체명은 200자 이하입니다.")
    private String name;

    @NotBlank(message = "주소는 필수 입력값입니다.")
    @Size(max = 255, message = "주소는 255자 이하입니다.")
    private String address;
}