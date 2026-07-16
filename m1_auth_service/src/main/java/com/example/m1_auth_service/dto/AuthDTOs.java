package com.example.m1_auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDTOs {
    
    @Data
    public static class LoginRequestDTO {
        @NotBlank @Email
        private String correoElectronico;
        
        @NotBlank @Size(min = 6)
        private String contrasena;
    }

    @Data
    public static class AuthResponseDTO {
        private Integer id;
        private Integer idRol;  // ✅ ID del profesor o estudiante
        private String nombre;
        private String correoElectronico;
        private String rol;
        private String token;
    }
}