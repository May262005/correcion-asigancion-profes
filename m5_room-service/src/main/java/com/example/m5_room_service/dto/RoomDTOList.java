package com.example.m5_room_service.dto;

import lombok.Data;

@Data
public class RoomDTOList {
    private Long id;
    private String nombre;
    private String abreviatura;
    private String ubicacion;
    private Integer capacidad;
    private String nombreEdificio; // Campo enriquecido gracias a la consulta del Feign Client
}