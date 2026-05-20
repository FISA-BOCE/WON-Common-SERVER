package com.woorifisa.won_common_server.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessStatus {

    OK(HttpStatus.OK, "OK"),
    CREATED(HttpStatus.CREATED, "CREATED"),
    NO_CONTENT(HttpStatus.NO_CONTENT, "NO_CONTENT"),

    MAPPING_STATUS_FOUND(HttpStatus.OK, "카드/증권 연결 상태 조회가 완료되었습니다."),
    CARD_USER_LINKED(HttpStatus.OK, "카드 연결 상태가 반영되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    SuccessStatus(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
