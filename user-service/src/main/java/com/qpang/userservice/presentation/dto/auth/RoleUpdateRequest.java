package com.qpang.userservice.presentation.dto.auth;

import com.qpang.common.entity.UserRole;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull(message = "권한 정보는 필수입니다.")
        UserRole role
) {
}
