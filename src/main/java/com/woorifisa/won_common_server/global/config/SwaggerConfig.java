package com.woorifisa.won_common_server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SERVICE_ID_SECURITY_SCHEME = "SERVICE_ID";
    private static final String INTERNAL_API_KEY_SECURITY_SCHEME = "INTERNAL_API_KEY";

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info().title("COMMON API 명세서")
                .description("공통계 Swagger UI입니다.")
                .version("0.0.1");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(SERVICE_ID_SECURITY_SCHEME)
                .addList(INTERNAL_API_KEY_SECURITY_SCHEME);

        Components components = new Components()
                .addSecuritySchemes(SERVICE_ID_SECURITY_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-Service-ID"))
                .addSecuritySchemes(INTERNAL_API_KEY_SECURITY_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-Internal-Api-Key"));

        return new OpenAPI()
                .info(info)
                .addServersItem(new Server().url("/"))
                .addSecurityItem(securityRequirement)
                .components(components);

    }

}
