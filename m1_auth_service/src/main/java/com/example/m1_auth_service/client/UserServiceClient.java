package com.example.m1_auth_service.client;

import com.example.m1_auth_service.dto.UsuarioRolDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 👈 Quita "url" y usa solo el nombre del servicio
@FeignClient(name = "m2-user-service")
public interface UserServiceClient {

    @GetMapping("/api/usuario/rol/{id}")
    UsuarioRolDTO getUsuarioRol(@PathVariable("id") Integer id);
}