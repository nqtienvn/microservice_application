package com.tien.common.config.security;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI(@Value("${open.api.title}") String title,
                           @Value("${open.api.version}") String version,
                           @Value("${open.api.description}") String description,
                           @Value("${open.api.url}") String url,
                           @Value("${open.api.description-server-url}") String descriptionServerUrl) {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description))
                .servers(List.of(new Server().url(url).description(descriptionServerUrl)));
    }

    @Bean
    public GroupedOpenApi groupOpenApi(
            @Value("${open.api.group}") String group,
            @Value("${open.api.scanPackage}") String scanPackage) {
        return GroupedOpenApi.builder()
                .group(group)
                .packagesToScan(scanPackage)
                .build();
    }
}
