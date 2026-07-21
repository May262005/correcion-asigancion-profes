package com.example.m11_psychology_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "m2-user-service")
public interface UserServiceClient {

    @GetMapping("/api/estudiantes/{id}")
    UserSummaryDto getEstudianteById(@PathVariable("id") Long id);

    @GetMapping("/api/profesores/{id}")
    UserSummaryDto getProfesorById(@PathVariable("id") Long id);
}
