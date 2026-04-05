package com.qpang.company.dto;

import com.qpang.company.domain.enums.CompanyStatus;
import lombok.Getter;

@Getter
public class CompanyStatusUpdateRequest {

    private CompanyStatus status;
}