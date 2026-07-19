package com.example.m4_building_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "edificio")
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

    // ============================================================
    // CONSTRUCTORES
    // ============================================================
    public BuildingEntity() {}

    public BuildingEntity(String nombre, String abreviatura, String tipo, Long idDivision) {
        this.nombre = nombre;
        this.abreviatura = abreviatura;
        this.tipo = tipo;
        this.idDivision = idDivision;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getIdDivision() {
        return idDivision;
    }

    public void setIdDivision(Long idDivision) {
        this.idDivision = idDivision;
    }
}