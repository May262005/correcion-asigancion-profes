package com.example.m4_building_service.dto;

import lombok.Data;

@Data
public class BuildingDTOList {
    private Long id;
    private String nombre;
    private String abreviatura;
    private String tipo;
    private Long idDivision;    
    private String nombreDivision;
}