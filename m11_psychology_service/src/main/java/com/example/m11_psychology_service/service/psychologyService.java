package com.example.m11_psychology_service.service;

import com.example.m11_psychology_service.client.UserServiceClient;
import com.example.m11_psychology_service.client.UserSummaryDto;
import com.example.m11_psychology_service.dto.CitaPsicologicaDto;
import com.example.m11_psychology_service.entity.CitaPsicologicaEntity;
import com.example.m11_psychology_service.entity.CitaPsicologicaEntity.EstadoCita;
import com.example.m11_psychology_service.repository.CitaPsicologicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class psychologyService {

    private final CitaPsicologicaRepository citaRepository;
    private final UserServiceClient userServiceClient;

    public CitaPsicologicaDto createCita(CitaPsicologicaDto dto) {
        CitaPsicologicaEntity entity = CitaPsicologicaEntity.builder()
                .idEstudiante(dto.getIdEstudiante())
                .idProfesor(dto.getIdProfesor())
                .fechaCita(dto.getFechaCita())
                .horaCita(dto.getHoraCita())
                .motivoConsulta(dto.getMotivoConsulta())
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoCita.pendiente)
                .build();
        return toDto(citaRepository.save(entity));
    }

    public List<CitaPsicologicaDto> getAllCitas() {
        return citaRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public CitaPsicologicaDto getCitaById(Long id) {
        return toDto(findCita(id));
    }

    public List<CitaPsicologicaDto> getCitasByEstudiante(Long idEstudiante) {
        return citaRepository.findByIdEstudiante(idEstudiante).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<CitaPsicologicaDto> getCitasByProfesor(Long idProfesor) {
        return citaRepository.findByIdProfesor(idProfesor).stream().map(this::toDto).collect(Collectors.toList());
    }

    public CitaPsicologicaDto updateEstado(Long id, EstadoCita estado) {
        CitaPsicologicaEntity entity = findCita(id);
        entity.setEstado(estado);
        return toDto(citaRepository.save(entity));
    }

    public void deleteCita(Long id) {
        citaRepository.delete(findCita(id));
    }

    private CitaPsicologicaEntity findCita(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));
    }

    private CitaPsicologicaDto toDto(CitaPsicologicaEntity e) {
        String nombreEstudiante = null;
        String nombreProfesor = null;

        try {
            UserSummaryDto estudiante = userServiceClient.getEstudianteById(e.getIdEstudiante());
            nombreEstudiante = estudiante != null ? estudiante.getNombreCompleto() : null;
        } catch (Exception ex) {
            nombreEstudiante = null;
        }

        try {
            UserSummaryDto profesor = userServiceClient.getProfesorById(e.getIdProfesor());
            nombreProfesor = profesor != null ? profesor.getNombreCompleto() : null;
        } catch (Exception ex) {
            nombreProfesor = null;
        }

        return CitaPsicologicaDto.builder()
                .id(e.getId())
                .idEstudiante(e.getIdEstudiante())
                .idProfesor(e.getIdProfesor())
                .nombreEstudiante(nombreEstudiante)
                .nombreProfesor(nombreProfesor)
                .fechaCita(e.getFechaCita())
                .horaCita(e.getHoraCita())
                .motivoConsulta(e.getMotivoConsulta())
                .estado(e.getEstado())
                .build();
    }
}