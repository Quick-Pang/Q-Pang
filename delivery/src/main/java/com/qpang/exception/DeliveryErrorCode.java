package com.qpang.exception;

import com.qpang.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DeliveryErrorCode implements ErrorCode {

    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "배송을 찾을 수 없습니다."),
    DELIVERY_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "배송 경로를 찾을 수 없습니다."),
    CURRENT_DELIVERY_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "현재 진행 중인 배송 경로가 없습니다."),

    DELIVERY_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 배송입니다."),
    DELIVERY_ROUTE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 배송 경로입니다."),

    INVALID_DELIVERY_INPUT(HttpStatus.BAD_REQUEST, "배송 요청 값이 올바르지 않습니다."),
    INVALID_DELIVERY_ROUTE_INPUT(HttpStatus.BAD_REQUEST, "배송 경로 요청 값이 올바르지 않습니다."),
    INVALID_DELIVERY_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 배송 상태입니다."),
    INVALID_DELIVERY_ROUTE_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 배송 경로 상태입니다.");

    private final HttpStatus status;
    private final String message;
}
