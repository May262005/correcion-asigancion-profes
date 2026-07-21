package com.example.m4_building_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuildingDto {
    private Long id;
    private String nombre;
    private String abreviatura;
    private String tipo;
    private Long idDivision;

    public static class Create {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;
        private String abreviatura;
        @NotBlank(message = "El tipo es obligatorio")
        private String tipo;
        @NotNull(message = "La división es obligatoria")
        private Long idDivision;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getAbreviatura() { return abreviatura; }
        public void setAbreviatura(String abreviatura) { this.abreviatura = abreviatura; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public Long getIdDivision() { return idDivision; }
        public void setIdDivision(Long idDivision) { this.idDivision = idDivision; }
    }

    public static class Update {
        private String nombre;
        private String abreviatura;
        private String tipo;
        private Long idDivision;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getAbreviatura() { return abreviatura; }
        public void setAbreviatura(String abreviatura) { this.abreviatura = abreviatura; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public Long getIdDivision() { return idDivision; }
        public void setIdDivision(Long idDivision) { this.idDivision = idDivision; }
    }
}