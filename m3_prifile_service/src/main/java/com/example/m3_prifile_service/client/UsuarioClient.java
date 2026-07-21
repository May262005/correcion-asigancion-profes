package com.example.m3_prifile_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "m2-user-service", fallback = UsuarioClientFallback.class)
public interface UsuarioClient {

    @GetMapping("/api/usuario/perfil/{id}")
    PerfilCompletoResponse obtenerPerfil(@PathVariable("id") Integer id);

    @PutMapping("/api/usuario/perfil/{id}")
    UsuarioBasicoDTO actualizarPerfil(@PathVariable("id") Integer id, @RequestBody ActualizarPerfilRequest dto);
}