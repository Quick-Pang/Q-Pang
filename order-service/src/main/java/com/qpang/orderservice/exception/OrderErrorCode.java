package com.qpang.orderservice.exception;

import com.qpang.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;



@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
    ORDER_ALREADY_DELETED(HttpStatus.NOT_FOUND,"이미 삭제된 주문입니다."),
    ORDER_ALREADY_CANCELED(HttpStatus.NOT_FOUND,"이미 취소된 주문입니다."),
    ORDER_ITEM_INVALID(HttpStatus.BAD_REQUEST, "주문 품목 입력이 올바르지 않습니다.");
    
    private final HttpStatus status;
    private final String message;
}
