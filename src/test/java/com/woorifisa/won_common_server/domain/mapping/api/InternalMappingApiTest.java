package com.woorifisa.won_common_server.domain.mapping.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woorifisa.won_common_server.domain.mapping.dto.response.MappingStatusResponse;
import com.woorifisa.won_common_server.domain.mapping.service.MappingService;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InternalMappingApi.class)
class InternalMappingApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MappingService mappingService;

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
}
