package com.woorifisa.won_common_server.domain.mapping.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCardUserMappingRequest(
        @NotNull(message = "카드 사용자 UUID는 필수입니다.")
        UUID cardUserUuid
) {
}
