package com.example.m12_mentorship_service.service;

import com.example.m12_mentorship_service.dto.AsesoriaDto;
import com.example.m12_mentorship_service.entity.AsesoriaEntity;
import com.example.m12_mentorship_service.entity.AsesoriaEntity.EstadoAsesoria;
import com.example.m12_mentorship_service.repository.AsesoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final AsesoriaRepository asesoriaRepository;

    public AsesoriaDto createAsesoria(AsesoriaDto dto) {
        AsesoriaEntity entity = AsesoriaEntity.builder()
                .idEstudiante(dto.getIdEstudiante())
                .idProfesor(dto.getIdProfesor())
                .idAsignatura(dto.getIdAsignatura())
                .fechaAsesoria(dto.getFechaAsesoria())
                .horaAsesoria(dto.getHoraAsesoria())
                .temaDuda(dto.getTemaDuda())
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoAsesoria.pendiente)
                .build();
        return toDto(asesoriaRepository.save(entity));
    }

    public List<AsesoriaDto> getAllAsesorias() {
        return asesoriaRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public AsesoriaDto getAsesoriaById(Long id) {
        return toDto(findAsesoria(id));
    }

    public List<AsesoriaDto> getAsesoriasByEstudiante(Long idEstudiante) {
        return asesoriaRepository.findByIdEstudiante(idEstudiante).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<AsesoriaDto> getAsesoriasByProfesor(Long idProfesor) {
        return asesoriaRepository.findByIdProfesor(idProfesor).stream().map(this::toDto).collect(Collectors.toList());
    }

    public AsesoriaDto updateEstado(Long id, EstadoAsesoria estado) {
        AsesoriaEntity entity = findAsesoria(id);
        entity.setEstado(estado);
        return toDto(asesoriaRepository.save(entity));
    }

    public void deleteAsesoria(Long id) {
        asesoriaRepository.delete(findAsesoria(id));
    }

    private AsesoriaEntity findAsesoria(Long id) {
        return asesoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asesoría no encontrada: " + id));
    }

    private AsesoriaDto toDto(AsesoriaEntity e) {
        return AsesoriaDto.builder()
                .id(e.getId())
                .idEstudiante(e.getIdEstudiante())
                .idProfesor(e.getIdProfesor())
                .idAsignatura(e.getIdAsignatura())
                .fechaAsesoria(e.getFechaAsesoria())
                .horaAsesoria(e.getHoraAsesoria())
                .temaDuda(e.getTemaDuda())
                .estado(e.getEstado())
                .build();
    }
}