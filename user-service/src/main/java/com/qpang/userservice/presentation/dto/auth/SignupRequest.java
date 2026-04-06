package com.qpang.userservice.presentation.dto.auth;

import com.qpang.common.entity.UserRole;
import com.qpang.userservice.application.dto.auth.SignupCommand;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupRequest(
        @NotBlank(message = "아이디는 필수입니다.")
        String username,
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,
        @NotNull(message = "권한 정보는 필수입니다.")
        UserRole role,
        UUID hubId,
        UUID companyId
) {
    public SignupCommand toCommand() {
        return SignupCommand.builder()
                .username(this.username)
                .password(this.password)
                .email(this.email)
                .nickname(this.nickname)
                .role(this.role)
                .hubId(this.hubId)
                .companyId(this.companyId)
                .build();
    }
}
