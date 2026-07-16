package com.example.m12_mentorship_service.dto;

import com.example.m12_mentorship_service.entity.AsesoriaEntity.EstadoAsesoria;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesoriaDto {

    private Long id;
    private Long idEstudiante;
    private Long idProfesor;
    private Long idAsignatura;
    private LocalDate fechaAsesoria;
    private LocalTime horaAsesoria;
    private String temaDuda;
    private EstadoAsesoria estado;
}