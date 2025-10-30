package com.tien.iam_service2_keycloak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/v3/api-docs/**") // chỉ mở cho swagger docs, cau hinh de cho phep fetch docs
                        .allowedOrigins("http://localhost:8080") // gateway
                        .allowedMethods("GET", "POST", "OPTIONS"); //prelight,
                // dùng option de hoi xem cho cho phép k, neu co thi quay lai dung get post
            }
        };
    }
}
