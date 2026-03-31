package com.qpang.company.dto;

import com.qpang.company.domain.entity.Company;
import com.qpang.company.domain.enums.CompanyStatus;
import com.qpang.company.domain.enums.CompanyType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CompanyResponse {

    private UUID id;
    private String name;
    private CompanyType type;
    private UUID hubId;
    private String address;
    private UUID managerUserId;
    private CompanyStatus status;

    public static CompanyResponse from(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .type(company.getType())
                .hubId(company.getHubId())
                .address(company.getAddress())
                .managerUserId(company.getManagerUserId())
                .status(company.getStatus())
                .build();
    }
}