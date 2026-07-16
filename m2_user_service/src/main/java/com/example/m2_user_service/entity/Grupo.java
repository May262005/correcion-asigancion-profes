package com.example.m2_user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "grupo")
@Data
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_turno")
    private Integer idTurno;

    @Column(name = "id_division")
    private Integer idDivision;

    @Column(name = "tutor_id")
    private Integer tutorId;

    private String nombre;
    private String abreviatura;
    private String grado; // <-- corregido: en BD es varchar(20), no integer

    @Column(name = "color_identificador")
    private String colorIdentificador;
}