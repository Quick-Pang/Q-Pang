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
@Table(name = "p_delivery_manager_users")
@DiscriminatorValue("DELIVERY_MANAGER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryManagerUser extends User {

    @Column(nullable = false)
    private UUID hubId;

    @Builder
    public DeliveryManagerUser(String username, String password, String email, String nickname, UserStatus status, UUID hubId) {
        super(username, password, email, nickname, UserRole.DELIVERY_MANAGER, status);
        this.hubId = hubId;
    }
}
