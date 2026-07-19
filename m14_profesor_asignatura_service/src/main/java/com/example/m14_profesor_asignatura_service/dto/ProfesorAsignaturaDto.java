package com.example.m14_profesor_asignatura_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

public class ProfesorAsignaturaDto {

    @Data
    public static class Create {
        @NotNull(message = "El profesor es obligatorio.")
        private Integer idProfesor;

        @NotNull(message = "La asignatura es obligatoria.")
        private Integer idAsignatura;

        @NotNull(message = "El periodo es obligatorio.")
        private Integer idPeriodo;

        private List<Integer> idGrupos;
    }

    @Data
    public static class Update {
        private Integer idProfesor;
        private Integer idAsignatura;
        private Integer idPeriodo;
        private List<Integer> idGrupos;
    }

    @Data
    public static class Response {
        private Integer id;
        private Integer idProfesor;
        private Integer idAsignatura;
        private Integer idPeriodo;
        private List<Integer> idGrupos;
    }
}