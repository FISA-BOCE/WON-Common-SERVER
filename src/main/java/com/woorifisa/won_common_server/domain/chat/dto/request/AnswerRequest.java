package com.woorifisa.won_common_server.domain.chat.dto.request;

import java.util.Map;

public record AnswerRequest(String originalMessage, String queryType, Map<String, Object> dbResult) {}