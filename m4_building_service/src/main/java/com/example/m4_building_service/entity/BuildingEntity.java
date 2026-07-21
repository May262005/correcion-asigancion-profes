package com.example.m4_building_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "edificio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 20)
    private String abreviatura;

    @Column(nullable = false)
    private String tipo;

    @Column(name = "id_division", nullable = false)
    private Long idDivision;
}