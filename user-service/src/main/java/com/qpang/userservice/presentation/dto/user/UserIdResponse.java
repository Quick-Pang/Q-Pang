package com.qpang.userservice.presentation.dto.user;

import java.util.UUID;

public record UserIdResponse(
        UUID userId
) {
    public static UserIdResponse from(UUID userId) {
        return new UserIdResponse(userId);
    }
}
