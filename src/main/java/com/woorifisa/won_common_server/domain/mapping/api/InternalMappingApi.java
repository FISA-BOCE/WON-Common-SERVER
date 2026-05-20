package com.woorifisa.won_common_server.domain.mapping.api;

import com.woorifisa.won_common_server.domain.mapping.dto.request.UpdateCardUserMappingRequest;
import com.woorifisa.won_common_server.domain.mapping.dto.request.UpdateInvestUserMappingRequest;
import com.woorifisa.won_common_server.domain.mapping.dto.response.MappingStatusResponse;
import com.woorifisa.won_common_server.domain.mapping.service.MappingService;
import com.woorifisa.won_common_server.global.response.ApiResponse;
import com.woorifisa.won_common_server.global.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Mapping API", description = "카드/증권 사용자 매핑 정보를 관리하는 내부 API")
@RequestMapping("/internal/mappings/users")
@RequiredArgsConstructor
public class InternalMappingApi {

    private final MappingService mappingService;


    @Operation(
            summary = "카드/증권 연결 상태 조회",
            description = "공통 사용자 UUID 기준으로 카드/증권 사용자 연결 상태를 조회합니다."
    )
    @GetMapping("/{userUuid}")
    public ResponseEntity<ApiResponse<MappingStatusResponse>> getMappingStatus(@PathVariable UUID userUuid) {
        MappingStatusResponse mappingStatusResponse = mappingService.getMappingStatus(userUuid);

        return ResponseEntity
                .status(SuccessStatus.MAPPING_STATUS_FOUND.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.MAPPING_STATUS_FOUND, mappingStatusResponse));
    }

    @Operation(
            summary = "카드 사용자 매핑 반영",
            description = "카드 사용자 생성 완료 후 공통 사용자 매핑 정보에 카드 사용자 UUID를 연결합니다."
    )
    @PatchMapping("/{userUuid}/card")
    public ResponseEntity<ApiResponse<MappingStatusResponse>> updateCardUserMapping(@PathVariable UUID userUuid,
                                                                                    @Valid @RequestBody UpdateCardUserMappingRequest updateCardUserMappingRequest) {
        MappingStatusResponse mappingStatusResponse = mappingService.updateCardUserMapping(userUuid, updateCardUserMappingRequest);

        return ResponseEntity
                .status(SuccessStatus.CARD_USER_LINKED.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.CARD_USER_LINKED, mappingStatusResponse));
    }


    @Operation(
            summary = "증권 사용자 매핑 반영",
            description = "증권 사용자 생성 완료 후 공통 사용자 매핑 정보에 증권 사용자 UUID를 연결합니다."
    )
    @PatchMapping("/{userUuid}/invest")
    public ResponseEntity<ApiResponse<MappingStatusResponse>> updateInvestUserMapping(@PathVariable UUID userUuid,
                                                                                      @Valid @RequestBody UpdateInvestUserMappingRequest updateInvestUserMappingRequest) {
        MappingStatusResponse mappingStatusResponse = mappingService.updateInvestUserMapping(userUuid, updateInvestUserMappingRequest);

        return ResponseEntity
                .status(SuccessStatus.INVEST_USER_LINKED.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.INVEST_USER_LINKED, mappingStatusResponse));
    }

}
