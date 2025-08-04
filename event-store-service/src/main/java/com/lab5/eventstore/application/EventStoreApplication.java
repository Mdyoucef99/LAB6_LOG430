package com.lab5.eventstore.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.lab5.eventstore"})
public class EventStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventStoreApplication.class, args);
    }
}