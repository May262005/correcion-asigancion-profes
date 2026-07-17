package com.example.m8_group_service.entity;

import jakarta.persistence.*;

/**
 * Mapea la tabla public.grupo
 *
 * CREATE TABLE public.grupo (
 *     id                   integer NOT NULL,
 *     id_turno             integer NOT NULL,          -- FK → turno (RESTRICT)
 *     id_division          integer,                   -- FK → division (SET NULL)
 *     tutor_id             integer,                   -- FK → profesor (SET NULL)
 *     nombre               character varying(100) NOT NULL UNIQUE,
 *     abreviatura          character varying(20),
 *     grado                character varying(20),
 *     color_identificador  character varying(7)
 * );
 *
 * Relaciones de salida manejadas como Integer (IDs planos)
 * para mantener el microservicio desacoplado de las entidades
 * de otros servicios (turno, division, profesor).
 */
@Entity
@Table(name = "grupo", schema = "public")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "grupo_seq")
    @SequenceGenerator(name = "grupo_seq",
                       sequenceName = "public.grupo_id_seq",
                       allocationSize = 1)
    private Integer id;

    /**
     * FK → turno.id  (NOT NULL, ON DELETE RESTRICT)
     * Se guarda como Integer plano; la validación de existencia
     * se delega al constraint de la BD.
     */
    @Column(name = "id_turno", nullable = false)
    private Integer idTurno;

    /**
     * FK → division.id  (ON DELETE SET NULL)
     */
    @Column(name = "id_division")
    private Integer idDivision;

    /**
     * FK → profesor.id  (ON DELETE SET NULL)
     */
    @Column(name = "tutor_id")
    private Integer tutorId;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "abreviatura", length = 20)
    private String abreviatura;

    @Column(name = "grado", length = 20)
    private String grado;

    /** Color HEX, p.ej. "#3A7BD5" */
    @Column(name = "color_identificador", length = 7)
    private String colorIdentificador;

    // ──────────────────────────────────────────────
    // Constructores
    // ──────────────────────────────────────────────
    public GroupEntity() {}

    // ──────────────────────────────────────────────
    // Getters / Setters
    // ──────────────────────────────────────────────
    public Integer getId()              { return id; }
    public void    setId(Integer id)    { this.id = id; }

    public Integer getIdTurno()             { return idTurno; }
    public void    setIdTurno(Integer t)    { this.idTurno = t; }

    public Integer getIdDivision()              { return idDivision; }
    public void    setIdDivision(Integer d)     { this.idDivision = d; }

    public Integer getTutorId()             { return tutorId; }
    public void    setTutorId(Integer t)    { this.tutorId = t; }

    public String  getNombre()              { return nombre; }
    public void    setNombre(String n)      { this.nombre = n; }

    public String  getAbreviatura()             { return abreviatura; }
    public void    setAbreviatura(String a)     { this.abreviatura = a; }

    public String  getGrado()               { return grado; }
    public void    setGrado(String g)       { this.grado = g; }

    public String  getColorIdentificador()              { return colorIdentificador; }
    public void    setColorIdentificador(String c)      { this.colorIdentificador = c; }
}