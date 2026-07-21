package com.example.m3_prifile_service.client;

import org.springframework.stereotype.Component;

@Component
public class UsuarioClientFallback implements UsuarioClient {
    @Override
    public PerfilCompletoResponse obtenerPerfil(Integer id) {
        return null; // ProfileService interpreta null como "servicio de usuarios no disponible"
    }
    @Override
    public UsuarioBasicoDTO actualizarPerfil(Integer id, ActualizarPerfilRequest dto) {
        return null;
    }
}