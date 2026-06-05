package com.woorifisa.won_common_server.domain.chat.external;

import com.woorifisa.won_common_server.domain.chat.dto.request.AnswerRequest;
import com.woorifisa.won_common_server.domain.chat.dto.response.ChatResponse;
import com.woorifisa.won_common_server.domain.chat.dto.response.ClassifyResponse;
import com.woorifisa.won_common_server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AzureAiWasClient {

    private final WebClient azureAiWasWebClient;

    public ClassifyResponse classify(String transactionId, String message) {
        ApiResponse<ClassifyResponse> response = azureAiWasWebClient.post()
                .uri("/api/ai/classify")
                .header("X-Transaction-ID", transactionId != null ? transactionId : "")
                .bodyValue(Map.of("message", message))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ClassifyResponse>>() {})
                .block();
        return response.data();
    }

    public ChatResponse generateAnswer(String transactionId, AnswerRequest request) {
        ApiResponse<ChatResponse> response = azureAiWasWebClient.post()
                .uri("/api/ai/answer")
                .header("X-Transaction-ID", transactionId != null ? transactionId : "")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ChatResponse>>() {})
                .block();
        return response.data();
    }
}