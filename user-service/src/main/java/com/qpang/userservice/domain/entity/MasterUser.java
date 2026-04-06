package com.qpang.userservice.domain.entity;

import com.qpang.common.entity.UserRole;
import com.qpang.common.entity.UserStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_master_users")
@DiscriminatorValue("MASTER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MasterUser extends User {

    @Builder
    public MasterUser(String username, String password, String email, String nickname, UserStatus status) {
        super(username, password, email, nickname, UserRole.MASTER, status);
    }
}
