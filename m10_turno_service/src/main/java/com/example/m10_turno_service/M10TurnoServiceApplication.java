package com.example.m10_turno_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class M10TurnoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(M10TurnoServiceApplication.class, args);
    }
}