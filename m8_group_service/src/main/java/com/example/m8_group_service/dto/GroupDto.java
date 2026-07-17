    package com.example.m8_group_service.dto;

    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Pattern;
    import jakarta.validation.constraints.Size;

    /**
     * DTO de Grupo Escolar.
     *
     * Usado tanto para peticiones (POST/PUT) como para respuestas (GET).
     * En respuestas el campo {@code id} viene relleno.
     * En peticiones de creación se omite {@code id}.
     */
    public class GroupDto {

        /** ID del grupo (solo en respuestas) */
        private Integer id;

        /** FK al turno — obligatorio */
        @NotNull(message = "El id_turno es obligatorio")
        private Integer idTurno;

        /** FK a la división — opcional */
        private Integer idDivision;

        /** FK al profesor-tutor — opcional */
        private Integer tutorId;

        @NotBlank(message = "El nombre del grupo es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        private String nombre;

        @Size(max = 20, message = "La abreviatura no puede superar los 20 caracteres")
        private String abreviatura;

        @Size(max = 20, message = "El grado no puede superar los 20 caracteres")
        private String grado;

        /**
         * Color identificador en formato HEX (#RRGGBB).
         * El regex acepta solo valores de 7 caracteres como "#3A7BD5".
         */
        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$",
                message = "El color debe tener formato HEX (#RRGGBB)")
        private String colorIdentificador;

        // ──────────────────────────────────────────────
        // Constructores
        // ──────────────────────────────────────────────
        public GroupDto() {}

        public GroupDto(Integer id, Integer idTurno, Integer idDivision,
                        Integer tutorId, String nombre, String abreviatura,
                        String grado, String colorIdentificador) {
            this.id                 = id;
            this.idTurno            = idTurno;
            this.idDivision         = idDivision;
            this.tutorId            = tutorId;
            this.nombre             = nombre;
            this.abreviatura        = abreviatura;
            this.grado              = grado;
            this.colorIdentificador = colorIdentificador;
        }

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