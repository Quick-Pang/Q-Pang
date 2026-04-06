package com.qpang.userservice.domain.entity;

import com.qpang.common.entity.UserRole;
import com.qpang.common.entity.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_supplier_manager_users")
@DiscriminatorValue("SUPPLIER_MANAGER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupplierManagerUser extends User {

    @Column(nullable = false)
    private UUID companyId;

    @Builder
    public SupplierManagerUser(String username, String password, String email, String nickname, UserStatus status, UUID companyId) {
        super(username, password, email, nickname, UserRole.SUPPLIER_MANAGER, status);
        this.companyId = companyId;
    }
}
