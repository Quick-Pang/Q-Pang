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
@Table(name = "p_hub_manager_users")
@DiscriminatorValue("HUB_MANAGER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubManagerUser extends User {

    @Column(nullable = false)
    private UUID hubId;

    @Builder
    public HubManagerUser(String username, String password, String email, String nickname, UserStatus status, UUID hubId) {
        super(username, password, email, nickname, UserRole.HUB_MANAGER, status);
        this.hubId = hubId;
    }
}
