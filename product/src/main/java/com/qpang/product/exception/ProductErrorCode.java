package com.qpang.product.exception;

import com.qpang.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 상태 코드를 찾을 수 없습니다."),
    PRODUCT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 상품입니다."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "재고 입력 수량은 1 이상이어야 합니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "단종된 상품은 상태를 변경할 수 없습니다."),
    CANNOT_CHANGE_STATUS_WITH_STOCK(HttpStatus.BAD_REQUEST, "재고가 있는 상품은 품절/단종으로 변경할 수 없습니다."),
    CANNOT_CHANGE_STATUS_WITHOUT_STOCK(HttpStatus.BAD_REQUEST, "재고가 없는 상품은 판매중으로 변경할 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}