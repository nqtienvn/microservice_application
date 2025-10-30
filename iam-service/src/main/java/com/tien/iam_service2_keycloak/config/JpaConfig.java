package com.tien.iam_service2_keycloak.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware") //chi dinh ra bean chiu trach nghiem thao tao lay ra "who"
public class JpaConfig {
}
