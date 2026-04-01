package com.qpang.product.exception;

import com.qpang.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 상품입니다."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "재고 수량은 1 이상이어야 합니다.");

    private final HttpStatus status;
    private final String message;
}