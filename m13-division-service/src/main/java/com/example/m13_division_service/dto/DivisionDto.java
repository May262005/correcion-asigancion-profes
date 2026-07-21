package com.example.m13_division_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTOs para el manejo de Divisiones
 * Contiene: Create, Update y Response
 */
@Data
public class DivisionDto {
    private Long id;
    private String nombre;
    private String abreviatura;

    // ============================================================
    // CREATE DTO
    // ============================================================
    @Data
    public static class Create {
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede tener más de 150 caracteres")
        private String nombre;

        @NotBlank(message = "La abreviatura es obligatoria")
        @Size(max = 20, message = "La abreviatura no puede tener más de 20 caracteres")
        private String abreviatura;
    }

    // ============================================================
    // UPDATE DTO
    // ============================================================
    @Data
    public static class Update {
        @Size(max = 150, message = "El nombre no puede tener más de 150 caracteres")
        private String nombre;

        @Size(max = 20, message = "La abreviatura no puede tener más de 20 caracteres")
        private String abreviatura;
    }

    // ============================================================
    // RESPONSE DTO
    // ============================================================
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private String nombre;
        private String abreviatura;

        // Método estático para convertir desde Entity (Corregido a Division)
        public static Response fromEntity(com.example.m13_division_service.entity.Division division) {
            return new Response(
                division.getId(),
                division.getNombre(),
                division.getAbreviatura()
            );
        }
    }
}