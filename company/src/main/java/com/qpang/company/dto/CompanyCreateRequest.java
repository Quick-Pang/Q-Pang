package com.qpang.company.dto;

import com.qpang.company.domain.enums.CompanyType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.validator.constraints.NotBlank;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CompanyCreateRequest {

    @NotBlank(message = "업체명은 필수 입력값입니다.")
    @Size(max = 200, message = "업체명은 200자 이하 입니다.")
    private String name;

    @NotNull
    private CompanyType type;

    @NotNull
    private UUID hubId;

    @NotBlank(message = "주소는 필수 입력값입니다.")
    @Size(max = 255, message = "주소는 255자 이하 입니다.")
    private String address;

    @NotNull
    private UUID managerUserId;
}
