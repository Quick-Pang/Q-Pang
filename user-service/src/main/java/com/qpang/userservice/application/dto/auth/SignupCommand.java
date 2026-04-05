package com.qpang.userservice.application.dto.auth;

import com.qpang.common.entity.UserRole;
import com.qpang.common.entity.UserStatus;
import com.qpang.userservice.domain.entity.*;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SignupCommand(
        String username,
        String password,
        String email,
        String nickname,
        UserRole role,
        UUID hubId,
        UUID companyId
) {
    // role 값에 따라 실제 저장할 하위 User 엔티티를 생성한다.
    public User toEntity(String encodedPassword) {
        return switch (role) {
            case MASTER -> MasterUser.builder()
                    .username(username)
                    .password(encodedPassword)
                    .email(email)
                    .nickname(nickname)
                    .status(UserStatus.PENDING)
                    .build();
            case HUB_MANAGER -> HubManagerUser.builder()
                    .username(username)
                    .password(encodedPassword)
                    .email(email)
                    .nickname(nickname)
                    .status(UserStatus.PENDING)
                    .hubId(hubId)
                    .build();
            case DELIVERY_MANAGER -> DeliveryManagerUser.builder()
                    .username(username)
                    .password(encodedPassword)
                    .email(email)
                    .nickname(nickname)
                    .status(UserStatus.PENDING)
                    .hubId(hubId)
                    .build();
            case SUPPLIER_MANAGER -> SupplierManagerUser.builder()
                    .username(username)
                    .password(encodedPassword)
                    .email(email)
                    .nickname(nickname)
                    .status(UserStatus.PENDING)
                    .companyId(companyId)
                    .build();
        };
    }
}
