package com.woorifisa.won_common_server.domain.chat.dto.response;

import java.util.Map;

public record ClassifyResponse(
        String queryType,
        String dbTarget,
        String dataSource,
        double confidence,
        Map<String, String> params
) {}