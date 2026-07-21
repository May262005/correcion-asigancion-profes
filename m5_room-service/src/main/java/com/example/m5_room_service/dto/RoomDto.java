package com.example.m5_room_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomDto {
    private Long id;
    private String nombre;
    private String abreviatura;
    private String ubicacion;
    private Integer capacidad;
    private Long idEdificio;

    public static class Create {
        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;
        private String abreviatura;
        @NotBlank(message = "La ubicación es obligatoria")
        private String ubicacion;
        @NotNull(message = "La capacidad es obligatoria")
        private Integer capacidad;
        @NotNull(message = "El ID del edificio es obligatorio")
        private Long idEdificio;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getAbreviatura() { return abreviatura; }
        public void setAbreviatura(String abreviatura) { this.abreviatura = abreviatura; }
        public String getUbicacion() { return ubicacion; }
        public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
        public Integer getCapacidad() { return capacidad; }
        public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
        public Long getIdEdificio() { return idEdificio; }
        public void setIdEdificio(Long idEdificio) { this.idEdificio = idEdificio; }
    }

    public static class Update {
        private String nombre;
        private String abreviatura;
        private String ubicacion;
        private Integer capacidad;
        private Long idEdificio;

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getAbreviatura() { return abreviatura; }
        public void setAbreviatura(String abreviatura) { this.abreviatura = abreviatura; }
        public String getUbicacion() { return ubicacion; }
        public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
        public Integer getCapacidad() { return capacidad; }
        public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
        public Long getIdEdificio() { return idEdificio; }
        public void setIdEdificio(Long idEdificio) { this.idEdificio = idEdificio; }
    }
}