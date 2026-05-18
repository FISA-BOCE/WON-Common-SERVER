package com.woorifisa.won_common_server.global.response;

import com.woorifisa.won_common_server.global.exception.code.SuccessStatus;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {

    public static <T> ApiResponse<T> of(SuccessStatus successStatus, T data) {
        return new ApiResponse<>(
                successStatus.getHttpStatus().value(),
                successStatus.getMessage(),
                data
        );
    }

    public static ApiResponse<Void> of(SuccessStatus successStatus) {
        return new ApiResponse<>(
                successStatus.getHttpStatus().value(),
                successStatus.getMessage(),
                null
        );
    }
}
