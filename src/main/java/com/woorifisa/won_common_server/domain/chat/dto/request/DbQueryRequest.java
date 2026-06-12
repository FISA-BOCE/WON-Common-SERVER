package com.woorifisa.won_common_server.domain.chat.dto.request;

import java.util.Map;

public record DbQueryRequest(String queryType, Map<String, String> params, String userUuid) {}