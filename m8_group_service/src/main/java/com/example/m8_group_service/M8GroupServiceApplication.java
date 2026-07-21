package com.example.m8_group_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients; // 👈 1. Importa esto

@EnableFeignClients // 👈 2. Agrega esta anotación obligatoria para que funcionen tus clients
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class M8GroupServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(M8GroupServiceApplication.class, args);
    }

}