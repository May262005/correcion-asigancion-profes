package com.example.m2_user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "profesor")
@Data
public class Profesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "id_usuario")
    private Integer idUsuario;
    
    @Column(name = "abreviatura_nombre", length = 20)
    private String abreviaturaNombre;
    
    private String telefono;
    private String titulo;
    
    @Column(name = "color_calendario", length = 7)
    private String colorCalendario = "#3abef9";
    
    @Column(name = "es_psicologo")
    private Boolean esPsicologo = false;
}