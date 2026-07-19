package com.example.m15_periodo_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class M15PeriodoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(M15PeriodoServiceApplication.class, args);
    }
}