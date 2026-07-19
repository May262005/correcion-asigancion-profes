package com.example.m14_profesor_asignatura_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class M14ProfesorAsignaturaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(M14ProfesorAsignaturaServiceApplication.class, args);
    }
}