package com.tien.storageservice_3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.tien.common", "com.tien.storageservice_3"})
@EnableDiscoveryClient
@EntityScan(basePackages = {"com.tien.common", "com.tien.storageservice_3.entity"})
public class StorageService3Application {

    public static void main(String[] args) {
        SpringApplication.run(StorageService3Application.class, args);
    }

}
