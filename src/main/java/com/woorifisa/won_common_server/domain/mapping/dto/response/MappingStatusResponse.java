package com.woorifisa.won_common_server.domain.mapping.dto.response;

import com.woorifisa.won_common_server.domain.mapping.model.CommUserMapping;

import java.util.UUID;

public record MappingStatusResponse(
        UUID userUuid,
        CardMappingStatus card,
        InvestMappingStatus invest
) {

    public static MappingStatusResponse from(CommUserMapping mapping) {
        return new MappingStatusResponse(
                mapping.getCommUser().getUserUuid(),
                new CardMappingStatus(
                        mapping.getCardUserUuid(),
                        mapping.isCardConnected()
                ),
                new InvestMappingStatus(
                        mapping.getInvestUserUuid(),
                        mapping.isInvestConnected()
                )
        );
    }

    public record CardMappingStatus(
            UUID cardUserUuid,
            boolean isConnected
    ) {
    }

    public record InvestMappingStatus(
            UUID investUserUuid,
            boolean isConnected
    ) {
    }
}
