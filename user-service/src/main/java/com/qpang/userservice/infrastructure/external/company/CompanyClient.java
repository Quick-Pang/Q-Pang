package com.qpang.userservice.infrastructure.external.company;

import com.qpang.common.response.APIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/companies/{id}")
    APIResponse<CompanyResponseDTO> getCompanyById(@PathVariable(name = "id") UUID id);
}
