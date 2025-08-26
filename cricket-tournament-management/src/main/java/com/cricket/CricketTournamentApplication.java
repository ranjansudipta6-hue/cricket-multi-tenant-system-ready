package com.cricket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.cricket", "com.example.multitenant"})
public class CricketTournamentApplication {
    public static void main(String[] args) {
        SpringApplication.run(CricketTournamentApplication.class, args);
    }
}
