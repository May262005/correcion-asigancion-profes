package com.example.m3_prifile_service.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO bidireccional para la entidad Avatar.
 *
 *  - Creación / actualización → se usa con {@code id = null}
 *  - Respuesta               → incluye el {@code id} generado
 */
public class AvatarDto {

    private Integer id;

    @NotBlank(message = "La imagen del avatar no puede estar vacía")
    @Size(max = 255, message = "La ruta de imagen no puede superar 255 caracteres")
    private String imagen;

    @Size(max = 200, message = "El nombre del avatar no puede superar 200 caracteres")
    private String nombre;

    // ──────────────────────────────────────────────
    // Constructores
    // ──────────────────────────────────────────────
    public AvatarDto() {}

    public AvatarDto(Integer id, String imagen, String nombre) {
        this.id     = id;
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