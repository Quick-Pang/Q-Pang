package com.qpang.userservice.presentation.dto.user;

import com.qpang.userservice.application.dto.user.UserUpdateCommand;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,
        @NotNull(message = "공개 여부는 필수입니다.")
        Boolean isPublic
) {
    public UserUpdateCommand toCommand() {
        return UserUpdateCommand.builder()
                .nickname(this.nickname)
                .email(this.email)
                .isPublic(this.isPublic)
                .build();
    }
}
