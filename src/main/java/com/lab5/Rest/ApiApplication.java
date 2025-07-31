package com.lab5.Rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.lab5.Rest", "com.lab5.dao", "com.lab5.Service", "com.lab5.Model", "com.lab5.Controller", "com.lab5.Rest.config"})
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
} 