package com.example.teacher.m6_teacher_service.dto;

import lombok.Data;

@Data
public class TeacherDto {
    private Integer id;
    private Integer idUsuario;
    private String abreviaturaNombre;
    private String telefono;
    private String titulo;
    private String colorCalendario;
    private Boolean esPsicologo;
}
