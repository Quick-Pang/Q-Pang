package com.qpang.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    // --- 공통 ---
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    MISSING_INPUT_VALUE(HttpStatus.BAD_REQUEST, "필수 값이 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),

    // --- 인증 ---
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // --- 리소스 ---
    NOT_FOUND(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다."),

    KAKAO_ROUTE_API_ERROR(HttpStatus.BAD_GATEWAY, "카카오 경로 API 호출에 실패했습니다."),
    INVALID_KAKAO_ROUTE_RESPONSE(HttpStatus.BAD_GATEWAY, "카카오 경로 API 응답이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;
}