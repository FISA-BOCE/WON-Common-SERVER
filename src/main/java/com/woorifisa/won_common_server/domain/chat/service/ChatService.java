package com.woorifisa.won_common_server.domain.chat.service;

import com.woorifisa.won_common_server.domain.chat.dto.request.ChatRequest;
import com.woorifisa.won_common_server.domain.chat.dto.response.ChatResponse;

public interface ChatService {
    ChatResponse processChat(String userUuid, String transactionId, ChatRequest request);
}