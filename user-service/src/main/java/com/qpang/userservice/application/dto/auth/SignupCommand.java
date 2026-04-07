package com.qpang.userservice.application.dto.auth;

import com.qpang.common.entity.UserRole;
import com.qpang.common.entity.UserStatus;
import com.qpang.common.exception.CustomException;
import com.qpang.userservice.domain.entity.*;
import com.qpang.userservice.exception.UserErrorCode;
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
        validateRoleSpecificFields();

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

    private void validateRoleSpecificFields() {
        switch (role) {
            case MASTER -> throw new CustomException(UserErrorCode.INVALID_SIGNUP_REQUEST);
            case HUB_MANAGER, DELIVERY_MANAGER -> {
                if (hubId == null) {
                    throw new CustomException(UserErrorCode.INVALID_SIGNUP_REQUEST);
                }
            }
            case SUPPLIER_MANAGER -> {
                if (companyId == null) {
                    throw new CustomException(UserErrorCode.INVALID_SIGNUP_REQUEST);
                }
            }
        }
    }
}
