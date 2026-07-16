package com.example.m12_mentorship_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "asesoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_estudiante", nullable = false)
    private Long idEstudiante;

    @Column(name = "id_profesor", nullable = false)
    private Long idProfesor;

    @Column(name = "id_asignatura")
    private Long idAsignatura;

    @Column(name = "fecha_asesoria", nullable = false)
    private LocalDate fechaAsesoria;

    @Column(name = "hora_asesoria", nullable = false)
    private LocalTime horaAsesoria;

    @Column(name = "tema_duda")
    private String temaDuda;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoAsesoria estado = EstadoAsesoria.pendiente;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    public enum EstadoAsesoria {
        pendiente, aceptada, rechazada, cancelada, completada
    }
}