package com.qpang.userservice.presentation.dto.auth;

import com.qpang.common.entity.UserRole;

public record RoleUpdateRequest(
        UserRole role
) {
}
