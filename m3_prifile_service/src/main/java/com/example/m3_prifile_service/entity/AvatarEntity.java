package com.example.m3_prifile_service.entity;

import jakarta.persistence.*;

/**
 * Mapea la tabla public.avatar
 *
 * CREATE TABLE public.avatar (
 *     id      integer NOT NULL,
 *     imagen  character varying(255) NOT NULL,
 *     nombre  text
 * );
 */
@Entity
@Table(name = "avatar", schema = "public")
public class AvatarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "avatar_seq")
    @SequenceGenerator(name = "avatar_seq",
                       sequenceName = "public.avatar_id_seq",
                       allocationSize = 1)
    private Integer id;

    /** Ruta o URL de la imagen del avatar */
    @Column(name = "imagen", nullable = false, length = 255)
    private String imagen;

    /** Nombre descriptivo del avatar */
    @Column(name = "nombre", columnDefinition = "text")
    private String nombre;

    // ──────────────────────────────────────────────
    // Constructores
    // ──────────────────────────────────────────────
    public AvatarEntity() {}

    public AvatarEntity(String imagen, String nombre) {
        this.imagen = imagen;
        this.nombre = nombre;
    }

    // ──────────────────────────────────────────────
    // Getters / Setters
    // ──────────────────────────────────────────────
    public Integer getId()              { return id; }
    public void    setId(Integer id)    { this.id = id; }

    public String  getImagen()              { return imagen; }
    public void    setImagen(String imagen) { this.imagen = imagen; }

    public String  getNombre()              { return nombre; }
    public void    setNombre(String nombre) { this.nombre = nombre; }
}