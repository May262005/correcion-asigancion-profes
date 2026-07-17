package com.example.m8_group_service.service;

import com.example.m8_group_service.dto.GroupDto;
import com.example.m8_group_service.entity.GroupEntity;
import com.example.m8_group_service.repository.GroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Servicio principal de m8_group_service.
 *
 * Gestiona el CRUD completo de grupos escolares y
 * provee consultas filtradas (por turno, división, tutor).
 */
@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    // ══════════════════════════════════════════════
    // CRUD básico
    // ══════════════════════════════════════════════

    /** Lista todos los grupos */
    public List<GroupDto> listarTodos() {
        return groupRepository.findAll()
                              .stream()
                              .map(this::toDto)
                              .collect(Collectors.toList());
    }

    /** Obtiene un grupo por su ID */
    public GroupDto obtenerPorId(Integer id) {
        GroupEntity entity = groupRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Grupo no encontrado con id: " + id));
        return toDto(entity);
    }

    /**
     * Crea un nuevo grupo escolar.
     * Valida que el nombre no exista ya en la BD.
     */
    @Transactional
    public GroupDto crear(GroupDto dto) {
        if (groupRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe un grupo con el nombre: " + dto.getNombre());
        }
        GroupEntity entity = toEntity(dto);
        return toDto(groupRepository.save(entity));
    }

    /**
     * Actualiza todos los campos de un grupo existente.
     * Valida unicidad de nombre solo si cambia.
     */
    @Transactional
    public GroupDto actualizar(Integer id, GroupDto dto) {
        GroupEntity entity = groupRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Grupo no encontrado con id: " + id));

        // Solo validar duplicado si el nombre cambia
        if (!entity.getNombre().equalsIgnoreCase(dto.getNombre())
                && groupRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException(
                "Ya existe un grupo con el nombre: " + dto.getNombre());
        }

        entity.setIdTurno(dto.getIdTurno());
        entity.setIdDivision(dto.getIdDivision());
        entity.setTutorId(dto.getTutorId());
        entity.setNombre(dto.getNombre());
        entity.setAbreviatura(dto.getAbreviatura());
        entity.setGrado(dto.getGrado());
        entity.setColorIdentificador(dto.getColorIdentificador());

        return toDto(groupRepository.save(entity));
    }

    /** Elimina un grupo. La BD aplica ON DELETE RESTRICT en estudiante. */
    @Transactional
    public void eliminar(Integer id) {
        if (!groupRepository.existsById(id)) {
            throw new NoSuchElementException("Grupo no encontrado con id: " + id);
        }
        groupRepository.deleteById(id);
    }

    // ══════════════════════════════════════════════
    // Consultas filtradas
    // ══════════════════════════════════════════════

    /** Grupos de un turno específico */
    public List<GroupDto> porTurno(Integer idTurno) {
        return groupRepository.findByIdTurno(idTurno)
                              .stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Grupos de una división */
    public List<GroupDto> porDivision(Integer idDivision) {
        return groupRepository.findByIdDivision(idDivision)
                              .stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Grupos asignados a un tutor */
    public List<GroupDto> porTutor(Integer tutorId) {
        return groupRepository.findByTutorId(tutorId)
                              .stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Grupos por división y grado (útil para el generador de horarios Gene/) */
    public List<GroupDto> porDivisionYGrado(Integer idDivision, String grado) {
        return groupRepository.findByDivisionAndGrado(idDivision, grado)
                              .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════
    // Mappers internos
    // ══════════════════════════════════════════════

    private GroupDto toDto(GroupEntity e) {
        return new GroupDto(
            e.getId(),
            e.getIdTurno(),
            e.getIdDivision(),
            e.getTutorId(),
            e.getNombre(),
            e.getAbreviatura(),
            e.getGrado(),
            e.getColorIdentificador()
        );
    }

    private GroupEntity toEntity(GroupDto dto) {
        GroupEntity e = new GroupEntity();
        e.setIdTurno(dto.getIdTurno());
        e.setIdDivision(dto.getIdDivision());
        e.setTutorId(dto.getTutorId());
        e.setNombre(dto.getNombre());
        e.setAbreviatura(dto.getAbreviatura());
        e.setGrado(dto.getGrado());
        e.setColorIdentificador(dto.getColorIdentificador());
        return e;
    }
}