package com.qpang.userservice.application.dto.user;

import com.qpang.common.entity.UserRole;
import com.qpang.common.entity.UserStatus;
import com.qpang.userservice.domain.entity.*;
import lombok.Builder;

import java.util.UUID;
import java.time.LocalDateTime;

@Builder
public record UserInfo(
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
    public static UserInfo from(User user) {
        UUID hubId = null;
        UUID companyId = null;

        if (user instanceof HubManagerUser h) {
            hubId = h.getHubId();
        } else if (user instanceof DeliveryManagerUser d) {
            hubId = d.getHubId();
        } else if (user instanceof SupplierManagerUser s) {
            companyId = s.getCompanyId();
        }

        return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .status(user.getStatus())
                .isPublic(user.getIsPublic())
                .createdAt(user.getCreatedAt())
                .hubId(hubId)
                .companyId(companyId)
                .build();
    }
}
