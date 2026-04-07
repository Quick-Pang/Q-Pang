package com.qpang.company.exception;

import com.qpang.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CompanyErrorCode implements ErrorCode {

    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "업체를 찾을 수 없습니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "허브를 찾을 수 없습니다."),
    COMPANY_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "업체 상태 코드를 찾을 수 없습니다."),
    COMPANY_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 업체입니다.");

    private final HttpStatus status;
    private final String message;
}