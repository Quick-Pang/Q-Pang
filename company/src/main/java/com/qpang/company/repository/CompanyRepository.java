package com.qpang.company.repository;

import com.qpang.company.domain.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    List<Company> findByHubId(UUID hubId);
    List<Company> findAllByHubIdAndDeletedAtIsNull(UUID hubId);
    List<Company> findAllByDeletedAtIsNull();
}