package com.example.m14_profesor_asignatura_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "profesor_aula")
@Data
public class ProfesorAulaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_profesor", nullable = false)
    private Integer idProfesor;

    @Column(name = "id_aula", nullable = false)
    private Integer idAula;

    @Column(name = "id_periodo", nullable = false)
    private Integer idPeriodo;
}