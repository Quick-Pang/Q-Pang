package com.qpang.company.repository;

import com.qpang.company.domain.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {


    List<Company> findAllByHubIdAndDeletedAtIsNull(UUID hubId);
    List<Company> findAllByDeletedAtIsNull();
    Page<Company> findAllByNameContainingAndDeletedAtIsNull(String name, Pageable pageable);
    Page<Company> findAllByHubIdAndNameContainingAndDeletedAtIsNull(UUID hubId, String name, Pageable pageable);

}