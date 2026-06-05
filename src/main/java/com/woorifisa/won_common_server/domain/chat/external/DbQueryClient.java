package com.woorifisa.won_common_server.domain.chat.external;

import com.woorifisa.won_common_server.domain.chat.dto.request.DbQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DbQueryClient {

    private final WebClient cardChannelWasWebClient;
    private final WebClient investChannelWasWebClient;

    public Map<String, Object> query(String queryType, String dbTarget, String dataSource,
                                     Map<String, String> params, String userUuid) {
        WebClient client;
        String uri;

        if ("SECURITIES".equals(dataSource)) {
            client = investChannelWasWebClient;
            uri = "/internal/invest/db/mysql/query";
        } else if ("NEO4J".equals(dbTarget)) {
            client = cardChannelWasWebClient;
            uri = "/internal/card/db/graph/query";
        } else {
            client = cardChannelWasWebClient;
            uri = "/internal/card/db/mysql/query";
        }

        Map<?, ?> response = client.post()
                .uri(uri)
                .header("X-Service-ID", "common-was")
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