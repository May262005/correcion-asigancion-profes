package com.example.m10_turno_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TurnoDto {

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

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public Integer getDiaInicio() { return diaInicio; }
        public void setDiaInicio(Integer diaInicio) { this.diaInicio = diaInicio; }

        public Integer getDiaFin() { return diaFin; }
        public void setDiaFin(Integer diaFin) { this.diaFin = diaFin; }

        public String getHoraInicio() { return horaInicio; }
        public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

        public String getHoraFin() { return horaFin; }
        public void setHoraFin(String horaFin) { this.horaFin = horaFin; }
    }

    public static class Update {
        private String nombre;
        private Integer diaInicio;
        private Integer diaFin;
        private String horaInicio;
        private String horaFin;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public Integer getDiaInicio() { return diaInicio; }
        public void setDiaInicio(Integer diaInicio) { this.diaInicio = diaInicio; }

        public Integer getDiaFin() { return diaFin; }
        public void setDiaFin(Integer diaFin) { this.diaFin = diaFin; }

        public String getHoraInicio() { return horaInicio; }
        public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

        public String getHoraFin() { return horaFin; }
        public void setHoraFin(String horaFin) { this.horaFin = horaFin; }
    }
}