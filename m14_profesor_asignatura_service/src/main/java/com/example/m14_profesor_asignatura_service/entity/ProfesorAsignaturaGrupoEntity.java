package com.example.m14_profesor_asignatura_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "profesor_asignatura_grupo")
@Data
public class ProfesorAsignaturaGrupoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_profesor_asignatura", nullable = false)
    private Integer idProfesorAsignatura;

    @Column(name = "id_grupo", nullable = false)
    private Integer idGrupo;
}