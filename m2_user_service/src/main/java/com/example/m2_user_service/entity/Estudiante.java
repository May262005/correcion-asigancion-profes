package com.example.m2_user_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "estudiante")
@Data
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "id_usuario")
    private Integer idUsuario;
    
    @Column(name = "id_grupo")
    private Integer idGrupo;
    
    private String matricula;
}