package com.qpang.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //CustomException
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

        ErrorCode errorCode = e.getErrorCode();

        log.error("CustomException: {}", errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.builder()
                        .success(false)
                        .status(errorCode.getStatus().value())
                        .errorCode(errorCode.getClass().getSimpleName() + "." + ((Enum<?>) errorCode).name())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Validation 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .success(false)
                        .status(400)
                        .errorCode("VALIDATION_ERROR")
                        .message(message)
                        .build());
    }

    //enum 에러
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

        log.error("HttpMessageNotReadableException: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .success(false)
                        .status(400)
                        .errorCode("INVALID_PRODUCT_STATUS")
                        .message("유효하지 않은 상태코드입니다.")
                        .build());
    }

    // 나머지 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("Exception: ", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .success(false)
                        .status(500)
                        .errorCode("INTERNAL_SERVER_ERROR")
                        .message("서버 오류가 발생했습니다.")
                        .build());
    }
}