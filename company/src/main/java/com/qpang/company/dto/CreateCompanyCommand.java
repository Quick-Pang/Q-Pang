package com.qpang.company.dto;

import com.qpang.company.domain.entity.Company;
import com.qpang.company.domain.enums.CompanyType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CreateCompanyCommand {

    private String name;
    private CompanyType type;
    private UUID hubId;
    private String address;
    private UUID managerUserId;

    public Company toEntity() {
        return Company.builder()
                .name(name)
                .type(type)
                .hubId(hubId)
                .address(address)
                .managerUserId(managerUserId)
                .build();
    }
}