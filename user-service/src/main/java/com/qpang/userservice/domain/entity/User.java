package com.qpang.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

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

    @Builder.Default
    @Column(nullable = false)
    private Boolean isPublic = true;

    // 비즈니스 로직
    public void approve() {
        this.status = UserStatus.APPROVED;
    }

    public void reject() {
        this.status = UserStatus.REJECTED;
    }

    public void updateInfo(String nickname, String email, Boolean isPublic) {
        this.nickname = nickname;
        this.email = email;
        this.isPublic = isPublic;
    }
}
