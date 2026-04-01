package com.qpang.company.domain.entity;

import com.qpang.common.entity.BaseUserEntity;
import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.company.domain.enums.CompanyStatus;
import com.qpang.company.domain.enums.CompanyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_company")
@NoArgsConstructor
public class Company extends BaseUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "company_name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", nullable = false, length = 30)
    private CompanyType type;

    @Column(name = "hub_id", nullable = false)
    private UUID hubId;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "manager_user_id", nullable = false)
    private UUID managerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_status", nullable = false)
    private CompanyStatus status;

    @Builder
    public Company(String name, CompanyType type, UUID hubId, String address, UUID managerUserId) {
        this.name = name;
        this.type = type;
        this.hubId = hubId;
        this.address = address;
        this.managerUserId = managerUserId;
        this.status = CompanyStatus.OPEN;
    }

    public void update(String name, String address) {
        if (name == null || name.isBlank()) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        if (address == null || address.isBlank()) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        this.name = name;
        this.address = address;
    }

    public void changeStatus(CompanyStatus status) {
        if (status == null) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        this.status = status;
    }

}