package com.example.curriculum.m7_curriculum_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "asignatura")
public class CurriculumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 150)
    private String nombre;

    @Column(length = 20)
    private String abreviatura;

    @Column(name = "color_identificador", length = 7)
    private String colorIdentificador;

    private String tipo;

    @Column(name = "horas_semanales")
    private Integer horasSemanales;

    @Column(name = "duracion_bloque_horas_min")
    private Integer duracionBloqueHorasMin;

    @Column(name = "duracion_bloque_horas_max")
    private Integer duracionBloqueHorasMax;

    @Column(name = "division_id")
    private Integer divisionId;
}
