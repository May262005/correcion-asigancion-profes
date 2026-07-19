package com.example.m14_profesor_asignatura_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class ProfesorAulaDto {

    @Data
    public static class Create {
        @NotNull(message = "El profesor es obligatorio.")
        private Integer idProfesor;

        @NotNull(message = "El aula es obligatoria.")
        private Integer idAula;

        @NotNull(message = "El periodo es obligatorio.")
        private Integer idPeriodo;
    }

    @Data
    public static class Update {
        private Integer idProfesor;
        private Integer idAula;
        private Integer idPeriodo;
    }
}