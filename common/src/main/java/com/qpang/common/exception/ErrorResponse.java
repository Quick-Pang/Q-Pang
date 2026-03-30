package com.qpang.common.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private boolean success;
    private int status;
    private String errorCode;
    private String message;
}