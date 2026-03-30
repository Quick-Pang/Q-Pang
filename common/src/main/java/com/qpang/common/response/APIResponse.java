package com.qpang.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class APIResponse<T> {
    private boolean success;
    private T data;

    public static <T> APIResponse<T> success(T data) {
        return APIResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
}
