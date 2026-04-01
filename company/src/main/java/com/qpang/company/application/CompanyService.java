package com.qpang.company.application;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.company.domain.entity.Company;
import com.qpang.company.domain.enums.CompanyStatus;
import com.qpang.company.domain.enums.CompanyType;
import com.qpang.company.repository.CompanyRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company create(String name, CompanyType type, UUID hubId, String address, UUID managerUserId) {
        return companyRepository.save(
                Company.builder()
                        .name(name)
                        .type(type)
                        .hubId(hubId)
                        .address(address)
                        .managerUserId(managerUserId)
                        .build()
        );
    }

    public List<Company> findAll() {
        return companyRepository.findAllByDeletedAtIsNull();
    }

    public Company findById(UUID id) {
        return companyRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));
    }

    public void update(UUID id, String name, String address) {
        Company company = findById(id);
        company.update(name, address);
    }

    public void changeStatus(UUID id, CompanyStatus status) {
        Company company = findById(id);
        company.changeStatus(status);
    }

    public void delete(UUID id, Long userId) {
        Company company = findById(id);
        company.delete(userId);
    }
}
