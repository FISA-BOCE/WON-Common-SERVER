package com.woorifisa.won_common_server.domain.mapping.exception;

import com.woorifisa.won_common_server.global.exception.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MappingErrorCode implements ErrorCode {

    MAPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "MAP_404_001", "고객 매핑 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MappingErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
