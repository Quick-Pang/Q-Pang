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

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotNull
    private CompanyType type;

    @NotNull
    private UUID hubId;

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotNull
    private UUID managerUserId;
}
