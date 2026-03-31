package com.sparta.company.application;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.sparta.company.domain.entity.Company;
import com.sparta.company.domain.enums.CompanyStatus;
import com.sparta.company.domain.enums.CompanyType;
import com.sparta.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company create(String name, CompanyType type, UUID hubId, String address, UUID managerUserId) {
        return companyRepository.save(new Company(name, type, hubId, address, managerUserId));
    }

    public List<Company> findAll() {
        return companyRepository.findAll()
                .stream()
                .filter(c -> c.getDeletedAt() == null)
                .toList();
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
