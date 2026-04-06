package com.qpang.company.application;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.company.client.HubClient;
import com.qpang.company.domain.entity.Company;
import com.qpang.company.domain.enums.CompanyStatus;
import com.qpang.company.domain.enums.CompanyType;
import com.qpang.company.dto.CreateCompanyCommand;
import com.qpang.company.exception.CompanyErrorCode;
import com.qpang.company.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final HubClient hubClient;

    public Company create(CreateCompanyCommand command) {
        //허브 존재 여부 확인
        try {
            hubClient.getHub(command.getHubId());
        } catch (Exception e) {
            throw new CustomException(CompanyErrorCode.HUB_NOT_FOUND);
        }
        Company company = command.toEntity();
        return companyRepository.save(company);
    }

    @Transactional(readOnly = true)
    public Company findById(UUID id) {
        return companyRepository.findById(id)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(CompanyErrorCode.COMPANY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<Company> search(UUID hubId, String name, int page, int size, String sortBy) {

        // 페이징 단위 기본 10, 30이나 50으로 볼 수 있음
        if (!List.of(10, 30, 50).contains(size)) {
            size = 10;
        }

        // 정렬 기준
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (hubId != null) {
            return companyRepository.findAllByHubIdAndNameContainingAndDeletedAtIsNull(hubId, name, pageable);
        }
        return companyRepository.findAllByNameContainingAndDeletedAtIsNull(name, pageable);
    }

    public void update(UUID id, String name, String address) {
        if (name == null || name.isBlank()) {
            throw new CustomException(CommonErrorCode.MISSING_INPUT_VALUE);
        }
        if (address == null || address.isBlank()) {
            throw new CustomException(CommonErrorCode.MISSING_INPUT_VALUE);
        }
        Company company = findById(id);
        company.update(name, address);
    }

    public void changeStatus(UUID id, CompanyStatus status) {
        if (status == null) {
            throw new CustomException(CommonErrorCode.MISSING_INPUT_VALUE);
        }
        Company company = findById(id);
        company.changeStatus(status);
    }

    public void delete(UUID id, Long userId) {
        Company company = findById(id);
        if (company.getDeletedAt() != null) {
            throw new CustomException(CompanyErrorCode.COMPANY_ALREADY_DELETED);
        }
//        company.delete(userId);
    }
}
