package com.qpang.userservice.domain.entity;


import com.qpang.common.entity.BaseUserEntity;
import com.qpang.common.entity.UserRole;
import com.qpang.common.entity.UserStatus;
import com.qpang.common.exception.CustomException;
import com.qpang.userservice.exception.UserErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_users")
@Inheritance(strategy = InheritanceType.JOINED)
// 역할별 하위 엔티티를 구분하기 위한 JPA 식별 컬럼.
@DiscriminatorColumn(name = "role_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User extends BaseUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private Boolean isPublic = true;

    public User(String username, String password, String email, String nickname, UserRole role, UserStatus status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.status = status;
        this.isPublic = true;
    }

    // PENDING 상태인 사용자만 승인되도록 상태 전이를 제한.
    public void approve() {
        if (this.status != UserStatus.PENDING) {
            throw new CustomException(UserErrorCode.ALREADY_PROCESSED);
        }
        this.status = UserStatus.APPROVED;
    }

    // PENDING 상태인 사용자만 거절되도록 상태 전이를 제한.
    public void reject() {
        if (this.status != UserStatus.PENDING) {
            throw new CustomException(UserErrorCode.ALREADY_PROCESSED);
        }
        this.status = UserStatus.REJECTED;
    }

    // 식별자와 권한 외의 프로필 정보만 변경.
    public void updateInfo(String nickname, String email, Boolean isPublic) {
        this.nickname = nickname;
        this.email = email;
        this.isPublic = isPublic;
    }

    // 관리자 요청에 따라 사용자 권한을 변경.
    public void updateRole(UserRole newRole) {
        this.role = newRole;
    }
}
