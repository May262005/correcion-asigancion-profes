package com.example.teacher.m6_teacher_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "profesor")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "abreviatura_nombre", length = 20)
    private String abreviaturaNombre;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String titulo;

    @Column(name = "color_calendario", length = 7)
    private String colorCalendario;

    @Column(name = "es_psicologo")
    private Boolean esPsicologo = false;
}
