package com.qpang.userservice.application.dto.auth;

import lombok.Builder;

@Builder
public record LoginCommand(
        String username,
        String password
) {
}
