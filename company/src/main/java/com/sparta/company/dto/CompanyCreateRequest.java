package com.sparta.company.dto;

import com.sparta.company.domain.enums.CompanyType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CompanyCreateRequest {

    private String name;
    private CompanyType type;
    private UUID hubId;
    private String address;
    private UUID managerUserId;
}
