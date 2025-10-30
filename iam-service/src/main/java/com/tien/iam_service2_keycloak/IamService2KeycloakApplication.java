package com.tien.iam_service2_keycloak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.tien.common.client.storage")
@ComponentScan(basePackages = {"com.tien.common", "com.tien.iam_service2_keycloak"})
@EntityScan(basePackages = {"com.tien.iam_service2_keycloak", "com.tien.common"})
@EnableDiscoveryClient
public class IamService2KeycloakApplication {
    public static void main(String[] args) {
        SpringApplication.run(IamService2KeycloakApplication.class, args);
    }
}
