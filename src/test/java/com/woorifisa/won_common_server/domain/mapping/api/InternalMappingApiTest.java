package com.woorifisa.won_common_server.domain.mapping.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woorifisa.won_common_server.domain.mapping.dto.request.UpdateCardUserMappingRequest;
import com.woorifisa.won_common_server.domain.mapping.dto.response.MappingStatusResponse;
import com.woorifisa.won_common_server.domain.mapping.service.MappingService;

import java.util.UUID;

import com.woorifisa.won_common_server.global.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InternalMappingApi.class)
@Import(GlobalExceptionHandler.class)
class InternalMappingApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MappingService mappingService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("카드/증권 연결 상태 조회 API 성공")
    void getMappingStatusSuccess() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID investUserUuid = UUID.fromString("cc1e08f6-f8ea-4b3e-8e41-85f94b473111");

        MappingStatusResponse response = new MappingStatusResponse(
                userUuid,
                new MappingStatusResponse.CardMappingStatus(null, false),
                new MappingStatusResponse.InvestMappingStatus(investUserUuid, true)
        );

        given(mappingService.getMappingStatus(any(UUID.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/internal/mappings/users/{userUuid}", userUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("카드/증권 연결 상태 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data.userUuid").value(userUuid.toString()))
                .andExpect(jsonPath("$.data.card.cardUserUuid").doesNotExist())
                .andExpect(jsonPath("$.data.card.isConnected").value(false))
                .andExpect(jsonPath("$.data.invest.investUserUuid").value(investUserUuid.toString()))
                .andExpect(jsonPath("$.data.invest.isConnected").value(true));
    }

    @Test
    @DisplayName("카드 사용자 매핑 반영 API 성공")
    void updateCardUserMappingSuccess() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");
        UUID cardUserUuid = UUID.fromString("4f8b3f2a-f7e6-43f8-b4df-a6729a671111");

        UpdateCardUserMappingRequest request = new UpdateCardUserMappingRequest(cardUserUuid);

        MappingStatusResponse response = new MappingStatusResponse(
                userUuid,
                new MappingStatusResponse.CardMappingStatus(cardUserUuid, true),
                new MappingStatusResponse.InvestMappingStatus(null, false)
        );

        given(mappingService.updateCardUserMapping(any(UUID.class), any(UpdateCardUserMappingRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(
                        patch("/internal/mappings/users/{userUuid}/card", userUuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Service-ID", "WON-CARD-CORE")
                                .header("X-Transaction-ID", "TX-20260512-MAP01")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("카드 연결 상태가 반영되었습니다."))
                .andExpect(jsonPath("$.data.userUuid").value(userUuid.toString()))
                .andExpect(jsonPath("$.data.card.cardUserUuid").value(cardUserUuid.toString()))
                .andExpect(jsonPath("$.data.card.isConnected").value(true))
                .andExpect(jsonPath("$.data.invest.investUserUuid").isEmpty())
                .andExpect(jsonPath("$.data.invest.isConnected").value(false));
    }

    @Test
    @DisplayName("카드 사용자 UUID가 누락되면 400을 반환한다")
    void updateCardUserMappingInvalidRequest() throws Exception {
        // given
        UUID userUuid = UUID.fromString("0a31e4b1-2b1d-4b5e-8b82-0fb48e502111");

        String requestBody = """
                {
                  "cardUserUuid": null
                }
                """;

        // when & then
        mockMvc.perform(
                        patch("/internal/mappings/users/{userUuid}/card", userUuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Service-ID", "WON-CARD-CORE")
                                .header("X-Transaction-ID", "TX-20260512-MAP01")
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("COM_400_001"))
                .andExpect(jsonPath("$.message").value("카드 사용자 UUID는 필수입니다."));
    }

}
