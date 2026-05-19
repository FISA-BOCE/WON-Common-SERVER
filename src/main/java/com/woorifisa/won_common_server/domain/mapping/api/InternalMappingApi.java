package com.woorifisa.won_common_server.domain.mapping.api;

import com.woorifisa.won_common_server.domain.mapping.dto.response.MappingStatusResponse;
import com.woorifisa.won_common_server.domain.mapping.service.MappingService;
import com.woorifisa.won_common_server.global.response.ApiResponse;
import com.woorifisa.won_common_server.global.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/mappings/users")
@RequiredArgsConstructor
public class InternalMappingApi {

    private final MappingService mappingService;

    @GetMapping("/{userUuid}")
    public ResponseEntity<ApiResponse<MappingStatusResponse>> getMappingStatus(@PathVariable UUID userUuid){
        MappingStatusResponse mappingStatusResponse = mappingService.getMappingStatus(userUuid);

        return ResponseEntity
                .status(SuccessStatus.MAPPING_STATUS_FOUND.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.MAPPING_STATUS_FOUND, mappingStatusResponse));
    }

}
