package com.example.m3_prifile_service.entity;
import jakarta.persistence.*;

/**
 * Vista reducida de public.usuario centrada en los campos
 * de personalización que gestiona m3_profile_service:
 *   - nombre, apellidos
 *   - correo_electronico
 *   - rol
 *   - fecha_registro
 *   - id_avatar  (FK → avatar)
 *
 * Los campos de autenticación (contrasena) se omiten
 * intencionalmente: ese dominio pertenece a otro microservicio.
 */
@Entity
@Table(name = "usuario", schema = "public")
public class UsuarioProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq",
                       sequenceName = "public.usuario_id_seq",
                       allocationSize = 1)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 100)
    private String apellidoMaterno;

    @Column(name = "correo_electronico", nullable = false, unique = true, length = 255)
    private String correoElectronico;

    /**
     * Se almacena pero NUNCA se expone en el DTO de respuesta.
     * Se incluye solo para satisfacer el constraint NOT NULL de la BD.
     */
    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    /**
     * Enum persistido como string usando el TYPE de PostgreSQL.
     * Valores posibles: 'admin', 'profesor', 'estudiante'
     */
    @Column(name = "rol", nullable = false, columnDefinition = "tipo_rol_usuario")
    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    @Column(name = "fecha_registro",
            columnDefinition = "timestamp(6) with time zone",
            insertable = false, updatable = false)
    private java.time.OffsetDateTime fechaRegistro;

    /** Relación Many-to-One con avatar — campo central de personalización */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_avatar",
                referencedColumnName = "id",
                foreignKey = @ForeignKey(name = "usuario_id_avatar_fkey"))
    private AvatarEntity avatar;

    // ──────────────────────────────────────────────
    // Enum interno
    // ──────────────────────────────────────────────
    public enum RolUsuario { admin, profesor, estudiante }

    // ──────────────────────────────────────────────
    // Constructores
    // ──────────────────────────────────────────────
    public UsuarioProfileEntity() {}

    // ──────────────────────────────────────────────
    // Getters / Setters
    // ──────────────────────────────────────────────
    public Integer getId()                          { return id; }
    public void    setId(Integer id)                { this.id = id; }

    public String  getNombre()                      { return nombre; }
    public void    setNombre(String nombre)         { this.nombre = nombre; }

    public String  getApellidoPaterno()                         { return apellidoPaterno; }
    public void    setApellidoPaterno(String apellidoPaterno)   { this.apellidoPaterno = apellidoPaterno; }

    public String  getApellidoMaterno()                         { return apellidoMaterno; }
    public void    setApellidoMaterno(String apellidoMaterno)   { this.apellidoMaterno = apellidoMaterno; }

    public String  getCorreoElectronico()                           { return correoElectronico; }
    public void    setCorreoElectronico(String correoElectronico)   { this.correoElectronico = correoElectronico; }

    public String  getContrasena()                  { return contrasena; }
    public void    setContrasena(String contrasena) { this.contrasena = contrasena; }

    public RolUsuario getRol()              { return rol; }
    public void       setRol(RolUsuario r)  { this.rol = r; }

    public java.time.OffsetDateTime getFechaRegistro()                              { return fechaRegistro; }
    public void                     setFechaRegistro(java.time.OffsetDateTime f)    { this.fechaRegistro = f; }

    public AvatarEntity getAvatar()             { return avatar; }
    public void         setAvatar(AvatarEntity a) { this.avatar = a; }
}