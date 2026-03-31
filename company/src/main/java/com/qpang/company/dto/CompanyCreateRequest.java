package com.qpang.company.dto;

import com.qpang.company.domain.enums.CompanyType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CompanyCreateRequest {

    private String name;
    private CompanyType type;
    private UUID hubId;
    private String address;
    private UUID managerUserId;
}
