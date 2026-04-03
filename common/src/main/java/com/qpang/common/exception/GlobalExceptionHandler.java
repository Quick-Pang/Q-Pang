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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

        log.error("HttpMessageNotReadableException: {}", e.getMessage());

        // enum 관련 오류인지 UUID 관련 오류인지 구분
        String message = e.getMessage();
        String errorCode;
        String errorMessage;

        if (message != null && message.contains("UUID")) {
            errorCode = "INVALID_INPUT_VALUE";
            errorMessage = "올바른 UUID 형식이 아닙니다.";
        } else {
            errorCode = "INVALID_INPUT_VALUE";
            errorMessage = "유효하지 않은 입력값입니다.";
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .success(false)
                        .status(400)
                        .errorCode(errorCode)
                        .message(errorMessage)
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