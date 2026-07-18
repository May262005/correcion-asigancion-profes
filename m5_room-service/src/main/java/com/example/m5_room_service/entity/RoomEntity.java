package com.example.m5_room_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "aula") // Mantiene el nombre exacto de la tabla en tu base de datos
@Data // Lombok genera automáticamente los getters, setters, toString, equals y hashCode
public class RoomEntity {

    // --- COLUMNAS PRINCIPALES ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Equivalente a PrimaryGeneratedColumn() (INT/INT4 autoincremental)

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 20) // Por defecto acepta nulos (nullable = true)
    private String abreviatura;

    @Column(length = 50) // Campo de ubicación
    private String ubicacion;

    @Column(nullable = false)
    private Integer capacidad;

    // --- RELACIÓN CON EDIFICIO EN MICROSERVICIOS ---
    // En lugar de mapear el objeto complejo Building, guardamos directamente
    // su identificador como un campo numérico Long.
    @Column(name = "id_edificio", nullable = false)
    private Long idEdificio;
}