package com.woorifisa.won_common_server.domain.chat.external;

import com.woorifisa.won_common_server.domain.chat.dto.request.AnswerRequest;
import com.woorifisa.won_common_server.domain.chat.dto.response.ChatResponse;
import com.woorifisa.won_common_server.domain.chat.dto.response.ClassifyResponse;
import com.woorifisa.won_common_server.domain.chat.exception.code.ChatErrorCode;
import com.woorifisa.won_common_server.global.exception.handler.BusinessException;
import com.woorifisa.won_common_server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AzureAiWasClient {

    private final WebClient azureAiWasWebClient;

    public ClassifyResponse classify(String transactionId, String message) {
        ApiResponse<ClassifyResponse> response = azureAiWasWebClient.post()
                .uri("/api/ai/classify")
                .headers(headers -> {
                    if (transactionId != null && !transactionId.isBlank()) {
                        headers.add("X-Transaction-ID", transactionId);
                    }
                })
                .bodyValue(Map.of("message", message))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ClassifyResponse>>() {})
                .switchIfEmpty(Mono.error(new BusinessException(ChatErrorCode.AI_WAS_ERROR)))
                .block();
        if (response == null || response.data() == null) {
            throw new BusinessException(ChatErrorCode.AI_WAS_ERROR);
        }
        return response.data();
    }

    public ChatResponse generateAnswer(String transactionId, AnswerRequest request) {
        ApiResponse<ChatResponse> response = azureAiWasWebClient.post()
                .uri("/api/ai/answer")
                .headers(headers -> {
                    if (transactionId != null && !transactionId.isBlank()) {
                        headers.add("X-Transaction-ID", transactionId);
                    }
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ChatResponse>>() {})
                .switchIfEmpty(Mono.error(new BusinessException(ChatErrorCode.AI_WAS_ERROR)))
                .block();
        if (response == null || response.data() == null) {
            throw new BusinessException(ChatErrorCode.AI_WAS_ERROR);
        }
        return response.data();
    }
}
