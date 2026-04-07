package com.qpang.userservice.application.dto.auth;

import com.qpang.userservice.application.dto.user.UserInfo;
import lombok.Builder;

@Builder
public record AuthTokenPair(
        UserInfo userInfo,
        String accessToken,
        String refreshToken
) {
}
