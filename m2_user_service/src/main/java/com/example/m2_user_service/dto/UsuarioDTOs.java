package com.example.m2_user_service.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

public class UsuarioDTOs {
    @Data
    public static class UsuarioResponseDTO {
        private Integer id;
        private String nombre;
        private String apellidoPaterno;
        private String apellidoMaterno;
        private String correoElectronico;
        private String rol;
        private Integer idAvatar;
    }

    @Data
    public static class ActualizarPerfilDTO {
        private String nombre;
        private String apellidoPaterno;
        private String apellidoMaterno;
        @Email
        private String correoElectronico;
        @Size(min = 6)
        private String password;
        private Integer idAvatar;
    }

    @Data
    public static class PerfilCompletoDTO {
        private UsuarioResponseDTO usuario;
        private Object extra;
    }
}