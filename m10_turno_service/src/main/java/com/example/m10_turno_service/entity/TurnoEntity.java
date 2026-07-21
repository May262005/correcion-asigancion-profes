package com.example.m10_turno_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "turno")
public class TurnoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "dia_inicio", nullable = false)
    private Integer diaInicio;

    @Column(name = "dia_fin", nullable = false)
    private Integer diaFin;

    @Column(name = "hora_inicio", nullable = false, length = 8)
    private String horaInicio;

    @Column(name = "hora_fin", nullable = false, length = 8)
    private String horaFin;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getDiaInicio() { return diaInicio; }
    public void setDiaInicio(Integer diaInicio) { this.diaInicio = diaInicio; }

    public Integer getDiaFin() { return diaFin; }
    public void setDiaFin(Integer diaFin) { this.diaFin = diaFin; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }
}