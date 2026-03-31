package com.sparta.company.domain.entity;

import com.qpang.common.entity.BaseUserEntity;
import com.sparta.company.domain.enums.CompanyStatus;
import com.sparta.company.domain.enums.CompanyType;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_company")
@NoArgsConstructor
public class Company extends BaseUserEntity {

    @Id
    @Column(name = "company_id")
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

}
