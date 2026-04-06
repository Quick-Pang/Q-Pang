package com.qpang.userservice.application.dto.user;

import lombok.Builder;

@Builder
public record UserUpdateCommand(
        String nickname,
        String email,
        Boolean isPublic
) {
}
