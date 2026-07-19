package com.example.m15_periodo_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

public class PeriodoDto {

    @Data
    public static class Create {
        @NotBlank(message = "El nombre del periodo es obligatorio.")
        private String nombre;

        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Boolean activo;
    }

    @Data
    public static class Update {
        private String nombre;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Boolean activo;
    }
}