package com.example.m10_turno_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "turno")
@Data
public class TurnoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "dia_inicio", nullable = false)
    private Integer diaInicio;

    @Column(name = "dia_fin", nullable = false)
    private Integer diaFin;

    @Column(name = "hora_inicio", nullable = false, length = 8)
    private String horaInicio;

    @Column(name = "hora_fin", nullable = false, length = 8)
    private String horaFin;
}