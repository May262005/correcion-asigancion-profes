package com.example.m3_prifile_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class M3PrifileServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(M3PrifileServiceApplication.class, args);
    }
}