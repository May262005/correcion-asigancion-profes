package com.example.m13_division_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "division")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Cambiado a Long para consistencia

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "abreviatura", length = 20, nullable = false)
    private String abreviatura;
}