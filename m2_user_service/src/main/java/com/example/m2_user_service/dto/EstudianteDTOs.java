package com.example.m2_user_service.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

public class EstudianteDTOs {
    
    @Data
    public static class CreateEstudianteDTO {
        @NotNull
        private Integer idGrupo;
        
        @NotBlank
        @Size(min = 5)
        private String matricula;
        
        @NotBlank
        @Size(min = 2)
        private String nombre;
        
        @NotBlank
        @Email
        private String correoElectronico;
        
        private String apellidoPaterno;
        private String apellidoMaterno;
        private String contrasena;
    }

    @Data
    public static class UpdateEstudianteDTO {
        private Integer idGrupo;
        private String matricula;
        private String nombre;
        private String apellidoPaterno;
        private String apellidoMaterno;
        
        @Email
        private String correoElectronico;
    }

    @Data
    public static class EstudianteResponseDTO {
        private Integer id;
        private String matricula;
        
        // 🔥 CAMBIO IMPORTANTE
        // Antes: private Integer idGrupo;
        // Ahora:
        private GrupoDTO grupo;
        
        private String nombreCompleto;
        private String correoElectronico;
    }
}