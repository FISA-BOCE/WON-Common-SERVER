package com.woorifisa.won_common_server.domain.chat.external;

import com.woorifisa.won_common_server.domain.chat.dto.request.DbQueryRequest;
import com.woorifisa.won_common_server.domain.chat.exception.code.ChatErrorCode;
import com.woorifisa.won_common_server.global.exception.handler.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class DbQueryClient {

    private static final String SERVICE_ID = "common-was";
    private static final String DATA_SOURCE_SECURITIES = "SECURITIES";
    private static final String DATA_SOURCE_CARD = "CARD";
    private static final String DB_TARGET_NEO4J = "NEO4J";
    private static final String URI_INVEST_MYSQL = "/internal/invest/db/mysql/query";
    private static final String URI_CARD_GRAPH = "/internal/card/db/graph/query";
    private static final String URI_CARD_MYSQL = "/internal/card/db/mysql/query";

    private final WebClient cardChannelWasWebClient;
    private final WebClient investChannelWasWebClient;
    private final String internalApiKey;

    public DbQueryClient(
            WebClient cardChannelWasWebClient,
            WebClient investChannelWasWebClient,
            @Value("${internal.channel.api-key}") String internalApiKey
    ) {
        this.cardChannelWasWebClient = cardChannelWasWebClient;
        this.investChannelWasWebClient = investChannelWasWebClient;
        this.internalApiKey = internalApiKey;
    }

    public Map<String, Object> query(String queryType, String dbTarget, String dataSource,
                                     Map<String, String> params, String userUuid) {
        WebClient client;
        String uri;

        if (DATA_SOURCE_SECURITIES.equals(dataSource)) {
            client = investChannelWasWebClient;
            uri = URI_INVEST_MYSQL;
        } else if (DATA_SOURCE_CARD.equals(dataSource) && DB_TARGET_NEO4J.equals(dbTarget)) {
            client = cardChannelWasWebClient;
            uri = URI_CARD_GRAPH;
        } else if (DATA_SOURCE_CARD.equals(dataSource)) {
            client = cardChannelWasWebClient;
            uri = URI_CARD_MYSQL;
        } else {
            throw new IllegalArgumentException(
                    "Unsupported query route. dataSource=" + dataSource + ", dbTarget=" + dbTarget
            );
        }

        Map<?, ?> response = client.post()
                .uri(uri)
                .header("X-Service-ID", SERVICE_ID)
                .header("X-Internal-Api-Key", internalApiKey)
                .header("X-User-UUID", userUuid)
                .bodyValue(new DbQueryRequest(queryType, params, userUuid))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !(response.get("data") instanceof Map<?, ?> data)) {
            throw new BusinessException(ChatErrorCode.DB_QUERY_ERROR);
        }
        Object result = data.get("result");
        if (result != null && !(result instanceof Map<?, ?>)) {
            throw new BusinessException(ChatErrorCode.DB_QUERY_ERROR);
        }
        return (Map<String, Object>) (result != null ? result : data);
    }
}