package com.example.m12_mentorship_service.service;

import com.example.m12_mentorship_service.client.UserServiceClient;
import com.example.m12_mentorship_service.client.UserSummaryDto;
import com.example.m12_mentorship_service.dto.AsesoriaDto;
import com.example.m12_mentorship_service.entity.AsesoriaEntity;
import com.example.m12_mentorship_service.entity.AsesoriaEntity.EstadoAsesoria;
import com.example.m12_mentorship_service.repository.AsesoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class mentorshipService {

    private final AsesoriaRepository asesoriaRepository;
    private final UserServiceClient userServiceClient;

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

    public List<String> getHorariosDisponibles(Long idProfesor, LocalDate fecha) {
        List<String> horariosBase = Arrays.asList(
                "09:00", "10:00", "11:00", "12:00", "14:00", "15:00", "16:00", "17:00"
        );

        List<String> ocupados = asesoriaRepository.findByIdProfesor(idProfesor).stream()
                .filter(asesoria -> fecha.equals(asesoria.getFechaAsesoria()))
                .map(AsesoriaEntity::getHoraAsesoria)
                .map(LocalTime::toString)
                .collect(Collectors.toList());

        return horariosBase.stream()
                .filter(horario -> !ocupados.contains(horario))
                .collect(Collectors.toList());
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

        return AsesoriaDto.builder()
                .id(e.getId())
                .idEstudiante(e.getIdEstudiante())
                .idProfesor(e.getIdProfesor())
                .nombreEstudiante(nombreEstudiante)
                .nombreProfesor(nombreProfesor)
                .idAsignatura(e.getIdAsignatura())
                .fechaAsesoria(e.getFechaAsesoria())
                .horaAsesoria(e.getHoraAsesoria())
                .temaDuda(e.getTemaDuda())
                .estado(e.getEstado())
                .build();
    }
}