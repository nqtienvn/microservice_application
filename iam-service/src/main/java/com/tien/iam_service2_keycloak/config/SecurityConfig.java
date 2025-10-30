package com.tien.iam_service2_keycloak.config;

import com.tien.common.config.security.KeyCloakSecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@ConditionalOnProperty(prefix = "iam", name = "use-keycloak", havingValue = "true")
public class SecurityConfig extends KeyCloakSecurityConfig {
    private final CustomerAccessFilter customerAccessFilter;
    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain keycloakFilterChain(HttpSecurity http) throws Exception {
        applyCommonConfig(http);
        http.addFilterAfter(customerAccessFilter, BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthConverter)));
        return http.build();
    }
}
