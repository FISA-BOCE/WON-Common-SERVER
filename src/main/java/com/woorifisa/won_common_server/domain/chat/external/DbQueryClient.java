package com.woorifisa.won_common_server.domain.chat.external;

import com.woorifisa.won_common_server.domain.chat.dto.request.DbQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DbQueryClient {

    private static final String SERVICE_ID = "common-was";
    private static final String DATA_SOURCE_SECURITIES = "SECURITIES";
    private static final String DB_TARGET_NEO4J = "NEO4J";
    private static final String URI_INVEST_MYSQL = "/internal/invest/db/mysql/query";
    private static final String URI_CARD_GRAPH = "/internal/card/db/graph/query";
    private static final String URI_CARD_MYSQL = "/internal/card/db/mysql/query";

    private final WebClient cardChannelWasWebClient;
    private final WebClient investChannelWasWebClient;

    public Map<String, Object> query(String queryType, String dbTarget, String dataSource,
                                     Map<String, String> params, String userUuid) {
        WebClient client;
        String uri;

        if (DATA_SOURCE_SECURITIES.equals(dataSource)) {
            client = investChannelWasWebClient;
            uri = URI_INVEST_MYSQL;
        } else if (DB_TARGET_NEO4J.equals(dbTarget)) {
            client = cardChannelWasWebClient;
            uri = URI_CARD_GRAPH;
        } else {
            client = cardChannelWasWebClient;
            uri = URI_CARD_MYSQL;
        }

        Map<?, ?> response = client.post()
                .uri(uri)
                .header("X-Service-ID", SERVICE_ID)
                .header("X-Internal-Api-Key", "local-test-internal-api-key")
                .header("X-User-UUID", userUuid)
                .bodyValue(new DbQueryRequest(queryType, params, userUuid))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map<?, ?> data = (Map<?, ?>) response.get("data");
        Object result = data.get("result");
        return result != null ? (Map<String, Object>) result : (Map<String, Object>) data;
    }
}