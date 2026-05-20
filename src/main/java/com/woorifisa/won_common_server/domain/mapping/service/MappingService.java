package com.woorifisa.won_common_server.domain.mapping.service;

import com.woorifisa.won_common_server.domain.mapping.dto.request.UpdateCardUserMappingRequest;
import com.woorifisa.won_common_server.domain.mapping.dto.response.MappingStatusResponse;
import com.woorifisa.won_common_server.domain.mapping.exception.MappingErrorCode;
import com.woorifisa.won_common_server.domain.mapping.model.CommUserMapping;
import com.woorifisa.won_common_server.domain.mapping.repository.CommUserMappingRepository;
import com.woorifisa.won_common_server.global.exception.handler.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MappingService {
    private final CommUserMappingRepository commUserMappingRepository;

    public MappingStatusResponse getMappingStatus(UUID userUuid) {
        CommUserMapping commUserMapping = commUserMappingRepository.findByCommUserUserUuid(userUuid)
                .orElseThrow(() -> new BusinessException(MappingErrorCode.MAPPING_NOT_FOUND));

        return MappingStatusResponse.from(commUserMapping);
    }

    @Transactional
    public MappingStatusResponse updateCardUserMapping(UUID userUuid, UpdateCardUserMappingRequest request) {
        CommUserMapping commUserMapping = commUserMappingRepository.findByCommUserUserUuid(userUuid)
                .orElseThrow(() -> new BusinessException(MappingErrorCode.MAPPING_NOT_FOUND));

        if (commUserMapping.isCardConnected()) {
            throw new BusinessException(MappingErrorCode.CARD_USER_ALREADY_LINKED);
        }

        commUserMapping.linkCardUser(request.cardUserUuid());

        return MappingStatusResponse.from(commUserMapping);

    }
}
