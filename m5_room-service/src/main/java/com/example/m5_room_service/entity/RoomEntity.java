package com.example.m5_room_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "aula")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 20)
    private String abreviatura;

    @Column(length = 50)
    private String ubicacion;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(name = "id_edificio", nullable = false)
    private Long idEdificio;
}