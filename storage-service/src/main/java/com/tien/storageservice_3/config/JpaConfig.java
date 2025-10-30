package com.tien.storageservice_3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware") //chi dinh ra bean chiu trach nghiem thao tao lay ra "who"
public class JpaConfig {
}
