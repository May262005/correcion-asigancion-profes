package com.example.m3_prifile_service.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de Perfil de Usuario para m3_profile_service.
 *
 * Expone únicamente los campos de personalización de interfaz:
 *   - Datos personales editables (nombre, apellidos)
 *   - Correo (sólo lectura en respuesta)
 *   - Rol   (sólo lectura en respuesta)
 *   - Avatar asociado
 *
 * La contraseña NUNCA se incluye en este DTO.
 */
public class ProfileDto {

    /** ID del usuario — presente en respuestas, nulo en creación */
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 100)
    private String apellidoPaterno;

    @Size(max = 100)
    private String apellidoMaterno;

    @Email(message = "El correo no tiene formato válido")
    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 255)
    private String correoElectronico;

    /** Sólo lectura — se devuelve en respuestas */
    private String rol;

    /** Fecha de registro — sólo lectura */
    private String fechaRegistro;

    /**
     * Avatar seleccionado por el usuario.
     * En peticiones de actualización se envía el {@code AvatarDto.id}.
     * En respuestas se devuelve el objeto completo.
     */
    private AvatarDto avatar;

    // ──────────────────────────────────────────────
    // Constructores
    // ──────────────────────────────────────────────
    public ProfileDto() {}

    // ──────────────────────────────────────────────
    // Getters / Setters
    // ──────────────────────────────────────────────
    public Integer getId()              { return id; }
    public void    setId(Integer id)    { this.id = id; }

    public String  getNombre()              { return nombre; }
    public void    setNombre(String n)      { this.nombre = n; }

    public String  getApellidoPaterno()             { return apellidoPaterno; }
    public void    setApellidoPaterno(String ap)    { this.apellidoPaterno = ap; }

    public String  getApellidoMaterno()             { return apellidoMaterno; }
    public void    setApellidoMaterno(String am)    { this.apellidoMaterno = am; }

    public String  getCorreoElectronico()               { return correoElectronico; }
    public void    setCorreoElectronico(String correo)  { this.correoElectronico = correo; }

    public String  getRol()             { return rol; }
    public void    setRol(String rol)   { this.rol = rol; }

    public String  getFechaRegistro()               { return fechaRegistro; }
    public void    setFechaRegistro(String fecha)   { this.fechaRegistro = fecha; }

    public AvatarDto getAvatar()                { return avatar; }
    public void      setAvatar(AvatarDto a)     { this.avatar = a; }
}