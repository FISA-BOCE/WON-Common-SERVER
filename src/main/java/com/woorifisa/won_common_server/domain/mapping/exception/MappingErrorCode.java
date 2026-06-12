package com.woorifisa.won_common_server.domain.mapping.exception;

import com.woorifisa.won_common_server.global.exception.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MappingErrorCode implements ErrorCode {

    MAPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "MAP_404_001", "고객 매핑 정보를 찾을 수 없습니다."),

    CARD_USER_ALREADY_LINKED(HttpStatus.CONFLICT, "MAP_409_001", "이미 카드 사용자와 연결되어 있습니다."),
    INVEST_USER_ALREADY_LINKED(HttpStatus.CONFLICT, "MAP_409_002", "이미 증권 사용자와 연결되어 있습니다."),
    CARD_USER_ALREADY_MAPPED(HttpStatus.CONFLICT,"MAP_409_003","이미 다른 사용자에게 연결된 카드 사용자입니다."),
    INVEST_USER_ALREADY_MAPPED(HttpStatus.CONFLICT, "MAP_409_004", "이미 다른 사용자에게 연결된 증권 사용자입니다.");

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
