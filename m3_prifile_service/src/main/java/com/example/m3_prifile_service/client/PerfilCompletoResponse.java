package com.example.m3_prifile_service.client;

// Espejo de m2.PerfilCompletoDTO — solo nos interesa el sub-objeto "usuario"
public class PerfilCompletoResponse {
    private UsuarioBasicoDTO usuario;
    public UsuarioBasicoDTO getUsuario() { return usuario; }
    public void setUsuario(UsuarioBasicoDTO usuario) { this.usuario = usuario; }
}