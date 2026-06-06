package com.woorifisa.won_common_server.domain.chat.service;

import com.woorifisa.won_common_server.domain.chat.dto.request.AnswerRequest;
import com.woorifisa.won_common_server.domain.chat.dto.request.ChatRequest;
import com.woorifisa.won_common_server.domain.chat.dto.response.ChatResponse;
import com.woorifisa.won_common_server.domain.chat.dto.response.ClassifyResponse;
import com.woorifisa.won_common_server.domain.chat.external.AzureAiWasClient;
import com.woorifisa.won_common_server.domain.chat.external.DbQueryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final double CONFIDENCE_THRESHOLD = 0.7;
    private static final String LOW_CONFIDENCE_RESPONSE = "죄송합니다. 질문을 좀 더 구체적으로 입력해 주세요. 예) '이번달 카드 결제 총액이 얼마야?'";

    private final AzureAiWasClient azureAiWasClient;
    private final DbQueryClient dbQueryClient;

    @Override
    public ChatResponse processChat(String userUuid, String transactionId, ChatRequest request) {
        ClassifyResponse classify = azureAiWasClient.classify(transactionId, request.message());

        if (classify.confidence() < CONFIDENCE_THRESHOLD) {
            return new ChatResponse(LOW_CONFIDENCE_RESPONSE);
        }

        Map<String, String> params = classify.params() != null ? classify.params() : Collections.emptyMap();
        Map<String, Object> dbResult = dbQueryClient.query(
                classify.queryType(), classify.dbTarget(), classify.dataSource(), params, userUuid
        );

        AnswerRequest answerRequest = new AnswerRequest(request.message(), classify.queryType(), dbResult);
        return azureAiWasClient.generateAnswer(transactionId, answerRequest);
    }
}
