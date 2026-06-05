package com.woorifisa.won_common_server.domain.chat.api;

import com.woorifisa.won_common_server.domain.chat.dto.request.ChatRequest;
import com.woorifisa.won_common_server.domain.chat.dto.response.ChatResponse;
import com.woorifisa.won_common_server.domain.chat.service.ChatService;
import com.woorifisa.won_common_server.global.response.ApiResponse;
import com.woorifisa.won_common_server.global.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/chats")
@Tag(name = "Internal Chat API", description = "AI 챗봇 오케스트레이션 내부 API")
public class InternalChatApi {

    private final ChatService chatService;

    @Operation(summary = "AI 챗봇 오케스트레이션", description = "사용자 질문을 AI 분류 → DB 조회 → 답변 생성 순서로 처리합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @RequestHeader("X-User-UUID") String userUuid,
            @RequestHeader(value = "X-Transaction-ID", required = false) String transactionId,
            @Valid @RequestBody ChatRequest request
    ) {
        ChatResponse response = chatService.processChat(userUuid, transactionId, request);
        return ResponseEntity
                .status(SuccessStatus.CHAT_ANSWER_GENERATED.getHttpStatus())
                .body(ApiResponse.of(SuccessStatus.CHAT_ANSWER_GENERATED, response));
    }
}