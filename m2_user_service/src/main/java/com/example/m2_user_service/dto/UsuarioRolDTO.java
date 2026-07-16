package com.example.m2_user_service.dto;

import lombok.Data;

@Data
public class UsuarioRolDTO {
    private Integer id;
    private String nombre;
    private String correoElectronico;
    private String rol;
    private Integer idRol;  // 👈 ID del profesor o estudiante
}