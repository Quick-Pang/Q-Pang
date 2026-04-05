package com.qpang.userservice.presentation.dto.user;

import com.qpang.common.entity.UserRole;
import com.qpang.common.entity.UserStatus;
import com.qpang.userservice.application.dto.user.UserInfo;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String nickname,
        UserRole role,
        UserStatus status,
        Boolean isPublic,
        LocalDateTime createdAt,
        UUID hubId,
        UUID companyId
) {
    public static UserResponse from(UserInfo userInfo) {
        return new UserResponse(
                userInfo.id(),
                userInfo.username(),
                userInfo.email(),
                userInfo.nickname(),
                userInfo.role(),
                userInfo.status(),
                userInfo.isPublic(),
                userInfo.createdAt(),
                userInfo.hubId(),
                userInfo.companyId()
        );
    }
}
