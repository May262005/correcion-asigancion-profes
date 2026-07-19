package com.example.m10_turno_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class TurnoDto {

    @Data
    public static class Create {
        @NotBlank(message = "El nombre del turno es obligatorio.")
        private String nombre;

        @NotNull(message = "El día de inicio es obligatorio.")
        private Integer diaInicio;

        @NotNull(message = "El día de fin es obligatorio.")
        private Integer diaFin;

        @NotBlank(message = "La hora de inicio es obligatoria.")
        private String horaInicio;

        @NotBlank(message = "La hora de fin es obligatoria.")
        private String horaFin;
    }

    @Data
    public static class Update {
        private String nombre;
        private Integer diaInicio;
        private Integer diaFin;
        private String horaInicio;
        private String horaFin;
    }
}