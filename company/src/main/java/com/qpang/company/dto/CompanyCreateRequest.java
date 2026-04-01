package com.qpang.company.dto;

import com.qpang.company.domain.enums.CompanyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.validator.constraints.NotBlank;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CompanyCreateRequest {

    @NotBlank
    private String name;

    @NotNull
    private CompanyType type;

    @NotNull
    private UUID hubId;

    @NotBlank
    private String address;

    @NotNull
    private UUID managerUserId;
}
