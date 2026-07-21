package com.example.m5_room_service.dto;

import lombok.Data;

@Data
public class BuildingDTO {
    private Long id;
    private String nombre;
    private String abreviatura;
    private String tipo;
    private Long idDivision;
}