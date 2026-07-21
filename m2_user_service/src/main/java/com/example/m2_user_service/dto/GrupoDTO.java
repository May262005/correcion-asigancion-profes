package com.example.m2_user_service.dto;

import lombok.Data;

@Data
public class GrupoDTO {

    private Integer id;

    private Integer idTurno;

    private Integer idDivision;

    private Integer tutorId;

    private String nombre;

    private String abreviatura;

    private String grado;

    private String colorIdentificador;
}