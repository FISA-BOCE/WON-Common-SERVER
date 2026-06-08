package com.woorifisa.won_common_server.global.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Value("${internal.services.ai-server.base-url}")
    private String azureAiWasUrl;

    @Value("${internal.services.card-channel.base-url}")
    private String cardChannelWasUrl;

    @Value("${internal.services.invest-channel.base-url}")
    private String investChannelWasUrl;

    @Bean
    public WebClient azureAiWasWebClient() {
        return WebClient.builder()
                .baseUrl(azureAiWasUrl)
                .defaultHeader("Content-Type", "application/json")
                .clientConnector(new ReactorClientHttpConnector(defaultHttpClient()))
                .build();
    }

    @Bean
    public WebClient cardChannelWasWebClient() {
        return WebClient.builder()
                .baseUrl(cardChannelWasUrl)
                .defaultHeader("Content-Type", "application/json")
                .clientConnector(new ReactorClientHttpConnector(defaultHttpClient()))
                .build();
    }

    @Bean
    public WebClient investChannelWasWebClient() {
        return WebClient.builder()
                .baseUrl(investChannelWasUrl)
                .defaultHeader("Content-Type", "application/json")
                .clientConnector(new ReactorClientHttpConnector(defaultHttpClient()))
                .build();
    }

    // connect timeout 5s, read timeout 30s
    private HttpClient defaultHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS)));
    }
}