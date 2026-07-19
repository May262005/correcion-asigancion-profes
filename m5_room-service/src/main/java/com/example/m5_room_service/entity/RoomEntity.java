package com.example.m5_room_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "aula")
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

    // ============================================================
    // CONSTRUCTORES
    // ============================================================
    public RoomEntity() {}

    public RoomEntity(String nombre, String abreviatura, String ubicacion, Integer capacidad, Long idEdificio) {
        this.nombre = nombre;
        this.abreviatura = abreviatura;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
        this.idEdificio = idEdificio;
    }

    // ============================================================
    // GETTERS Y SETTERS
    // ============================================================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Long getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(Long idEdificio) {
        this.idEdificio = idEdificio;
    }
}