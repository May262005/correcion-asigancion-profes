package com.example.m2_user_service.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

public class ProfesorDTOs {
    @Data
    public static class CreateProfesorDTO {
        @NotBlank @Size(min = 2)
        private String nombre;
        private String apellidoPaterno;
        private String apellidoMaterno;
        @NotBlank @Email
        private String correoElectronico;
        private String telefono;
        private String titulo;
        private Boolean esPsicologo = false;
    }

    @Data
    public static class UpdateProfesorDTO {
        private String nombre;
        private String apellidoPaterno;
        private String apellidoMaterno;
        @Email
        private String correoElectronico;
        private String telefono;
        private String titulo;
        private Boolean esPsicologo;
    }

    @Data
    public static class ProfesorResponseDTO {
        private Integer id;
        private String nombreCompleto;
        private String correoElectronico;
        private String telefono;
        private String titulo;
        private Boolean esPsicologo;
        private String abreviatura;
    }
}