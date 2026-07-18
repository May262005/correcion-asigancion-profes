package com.example.m5_room_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class RoomDto {

    // ==========================================
    // 1. DTO PARA CREACIÓN
    // ==========================================
    @Data
    @Schema(description = "Modelo para la creación de un aula")
    public static class Create {

        @NotNull(message = "El ID del edificio es obligatorio.")
        @Schema(description = "ID del edificio al que pertenece el aula", example = "1")
        private Long idEdificio; // Cambiado a camelCase para seguir las convenciones de Java

        @NotBlank(message = "El nombre del aula es obligatorio.")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
        @Schema(description = "Nombre del aula (ej: Aula Magna)", example = "Aula Magna")
        private String nombre;

        @Size(max = 20, message = "La abreviatura no puede superar los 20 caracteres.")
        @Schema(description = "Abreviatura corta del aula (ej: AM)", example = "AM", nullable = true)
        private String abreviatura; // Opcional por defecto al no tener @NotBlank

        @Size(max = 50, message = "La ubicación no puede superar los 50 caracteres.")
        @Schema(description = "Ubicación dentro del edificio", example = "2do piso, ala sur", nullable = true)
        private String ubicacion; // Opcional por defecto

        @NotNull(message = "La capacidad es obligatoria.")
        @Min(value = 1, message = "La capacidad debe ser de al menos 1 estudiante.")
        @Schema(description = "Capacidad máxima de estudiantes", example = "35")
        private Integer capacidad;
    }

    // ==========================================
    // 2. DTO PARA ACTUALIZACIÓN (Propiedades Opcionales)
    // ==========================================
    @Data
    @Schema(description = "Modelo para la actualización parcial de un aula")
    public static class Update {

        @Schema(description = "ID del edificio al que pertenece el aula", example = "1", nullable = true)
        private Long idEdificio;

        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
        @Schema(description = "Nombre del aula (ej: Aula Magna)", example = "Aula de Cómputo 1", nullable = true)
        private String nombre;

        @Size(max = 20, message = "La abreviatura no puede superar los 20 caracteres.")
        @Schema(description = "Abreviatura corta del aula (ej: AM)", example = "AC-1", nullable = true)
        private String abreviatura;

        @Size(max = 50, message = "La ubicación no puede superar los 50 caracteres.")
        @Schema(description = "Ubicación dentro del edificio", example = "1er piso, ala norte", nullable = true)
        private String ubicacion;

        @Min(value = 1, message = "La capacidad debe ser de al menos 1 estudiante.")
        @Schema(description = "Capacidad máxima de estudiantes", example = "40", nullable = true)
        private Integer capacidad;
    }
}