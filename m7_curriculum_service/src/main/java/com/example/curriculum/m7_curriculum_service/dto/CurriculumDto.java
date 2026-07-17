package com.example.curriculum.m7_curriculum_service.dto;

import lombok.Data;

@Data
public class CurriculumDto {
    private Integer id;
    private String nombre;
    private String abreviatura;
    private String colorIdentificador;
    private String tipo;
    private Integer horasSemanales;
    private Integer duracionBloqueHorasMin;
    private Integer duracionBloqueHorasMax;
    private Integer divisionId;
}
