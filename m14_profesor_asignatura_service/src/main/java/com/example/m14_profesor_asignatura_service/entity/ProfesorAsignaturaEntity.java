package com.example.m14_profesor_asignatura_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "profesor_asignatura")
@Data
public class ProfesorAsignaturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_profesor", nullable = false)
    private Integer idProfesor;

    @Column(name = "id_asignatura", nullable = false)
    private Integer idAsignatura;

    @Column(name = "id_periodo", nullable = false)
    private Integer idPeriodo;
}