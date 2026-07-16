package com.example.m1_auth_service.dto;

import lombok.Data;

@Data
public class UsuarioRolDTO {
    private Integer id;
    private String nombre;
    private String correoElectronico;
    private String rol;
    private Integer idRol;  // 👈 ID del profesor o estudiante
}