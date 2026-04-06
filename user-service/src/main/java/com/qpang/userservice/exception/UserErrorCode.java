package com.qpang.userservice.exception;

import com.qpang.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "중복된 사용자명입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일입니다."),
    INVALID_SIGNUP_REQUEST(HttpStatus.BAD_REQUEST, "회원가입 요청값이 올바르지 않습니다."),
    NOT_APPROVED_USER(HttpStatus.FORBIDDEN, "가입 승인되지 않은 사용자입니다."),
    ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 사용자 상태입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
