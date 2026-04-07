package com.qpang.userservice.presentation.dto.auth;

import com.qpang.userservice.application.dto.auth.LoginCommand;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "아이디는 필수입니다.")
        String username,
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
    public LoginCommand toCommand() {
        return LoginCommand.builder()
                .username(this.username)
                .password(this.password)
                .build();
    }
}
