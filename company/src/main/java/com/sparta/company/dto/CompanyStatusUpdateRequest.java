package com.sparta.company.dto;

import com.sparta.company.domain.enums.CompanyStatus;
import lombok.Getter;

@Getter
public class CompanyStatusUpdateRequest {

    private CompanyStatus status;
}