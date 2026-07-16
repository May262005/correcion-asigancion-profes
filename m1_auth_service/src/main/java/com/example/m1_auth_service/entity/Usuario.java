package com.example.m1_auth_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
    
    @Column(name = "apellido_paterno", length = 100, nullable = false)
    private String apellidoPaterno;
    
    @Column(name = "apellido_materno", length = 100, nullable = false)
    private String apellidoMaterno;
    
    @Column(name = "correo_electronico", length = 255, unique = true, nullable = false)
    private String correoElectronico;
    
    @Column(name = "contrasena", length = 255, nullable = false)
    private String contrasena;
    
    @Column(name = "rol", nullable = false)
    private String rol;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}