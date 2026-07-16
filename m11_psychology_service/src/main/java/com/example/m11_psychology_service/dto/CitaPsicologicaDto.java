package com.example.m11_psychology_service.dto;

import com.example.m11_psychology_service.entity.CitaPsicologicaEntity.EstadoCita;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaPsicologicaDto {

    private Long id;
    private Long idEstudiante;
    private Long idProfesor;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String motivoConsulta;
    private EstadoCita estado;
}