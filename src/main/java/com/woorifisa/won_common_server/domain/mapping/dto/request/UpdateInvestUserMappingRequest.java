package com.woorifisa.won_common_server.domain.mapping.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateInvestUserMappingRequest(
        @NotNull(message = "증권 사용자 UUID는 필수입니다.")
        UUID investUserUuid
) {
}
