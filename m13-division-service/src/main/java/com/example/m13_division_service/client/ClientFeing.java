package com.example.m13_division_service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "m13-division-service")
public interface ClientFeing {
    // Aquí puedes declarar métodos de comunicación con otros servicios si lo requieres
}