package com.qpang.userservice.infrastructure.external.company;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/companies/{id}")
    CompanyResponseDTO getCompanyById(@PathVariable(name = "id") UUID id);
}
