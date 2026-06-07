package com.woorifisa.won_common_server.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woorifisa.won_common_server.global.exception.code.CommonErrorCode;
import com.woorifisa.won_common_server.global.response.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class InternalApiAuthFilter extends OncePerRequestFilter {

    private static final String SERVICE_ID_HEADER = "X-Service-ID";
    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";
    private static final String INTERNAL_API_PATH_PREFIX = "/internal/";
    private static final String INTERNAL_ROLE = "ROLE_INTERNAL";

    private final Set<String> allowedServiceIds;
    private final String internalApiKey;
    private final ObjectMapper objectMapper;

    public InternalApiAuthFilter(
            @Value("${internal.auth.allowed-service-ids:}") String allowedServiceIds,
            @Value("${internal.auth.api-key:}") String internalApiKey,
            ObjectMapper objectMapper
    ) {
        this.allowedServiceIds = Arrays.stream(allowedServiceIds.split(","))
                .map(String::trim)
                .filter(serviceId -> !serviceId.isBlank())
                .collect(Collectors.toUnmodifiableSet());
        this.internalApiKey = normalize(internalApiKey);
        this.objectMapper = objectMapper;
        validateInternalAuthProperties();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !(uri.equals("/internal") || uri.startsWith(INTERNAL_API_PATH_PREFIX));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String serviceId = request.getHeader(SERVICE_ID_HEADER);
        String apiKey = request.getHeader(INTERNAL_API_KEY_HEADER);

        if (!isAuthorized(serviceId, apiKey)) {
            writeUnauthorizedResponse(response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                serviceId,
                null,
                java.util.List.of(new SimpleGrantedAuthority(INTERNAL_ROLE))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean isAuthorized(String serviceId, String apiKey) {
        if (allowedServiceIds.isEmpty() || !hasText(internalApiKey)) {
            log.error("내부 API 인증 설정이 누락되었습니다.");
            return false;
        }

        return hasText(serviceId)
                && hasText(apiKey)
                && allowedServiceIds.contains(serviceId)
                && constantTimeEquals(internalApiKey, apiKey);
    }

    private void validateInternalAuthProperties() {
        if (allowedServiceIds.isEmpty() || !hasText(internalApiKey)) {
            log.error("internal.auth.allowed-service-ids and internal.auth.api-key must not be blank.");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean constantTimeEquals(String expected, String actual) {
        if (!hasText(expected) || actual == null) {
            return false;
        }

        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = actual.getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        CommonErrorCode errorCode = CommonErrorCode.UNAUTHORIZED;

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.of(errorCode)));
    }
}
