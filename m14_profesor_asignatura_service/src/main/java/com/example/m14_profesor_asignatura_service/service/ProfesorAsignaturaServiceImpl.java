package com.example.m14_profesor_asignatura_service.service;

import com.example.m14_profesor_asignatura_service.dto.ProfesorAsignaturaDto;
import com.example.m14_profesor_asignatura_service.entity.ProfesorAsignaturaEntity;
import com.example.m14_profesor_asignatura_service.entity.ProfesorAsignaturaGrupoEntity;
import com.example.m14_profesor_asignatura_service.repository.ProfesorAsignaturaGrupoRepository;
import com.example.m14_profesor_asignatura_service.repository.ProfesorAsignaturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfesorAsignaturaServiceImpl implements ProfesorAsignaturaService {

    private final ProfesorAsignaturaRepository asignaturaRepository;
    private final ProfesorAsignaturaGrupoRepository grupoRepository;

    public ProfesorAsignaturaServiceImpl(ProfesorAsignaturaRepository asignaturaRepository,
                                          ProfesorAsignaturaGrupoRepository grupoRepository) {
        this.asignaturaRepository = asignaturaRepository;
        this.grupoRepository = grupoRepository;
    }

    @Override
    @Transactional
    public ProfesorAsignaturaDto.Response create(ProfesorAsignaturaDto.Create dto) {
        ProfesorAsignaturaEntity entity = new ProfesorAsignaturaEntity();
        entity.setIdProfesor(dto.getIdProfesor());
        entity.setIdAsignatura(dto.getIdAsignatura());
        entity.setIdPeriodo(dto.getIdPeriodo());
        ProfesorAsignaturaEntity saved = asignaturaRepository.save(entity);

        guardarGrupos(saved.getId(), dto.getIdGrupos());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesorAsignaturaDto.Response> findAll() {
        return asignaturaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProfesorAsignaturaDto.Response findOne(Integer id) {
        ProfesorAsignaturaEntity entity = asignaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación con ID \"" + id + "\" no encontrada."));
        return toResponse(entity);
    }

    @Override
    @Transactional
    public ProfesorAsignaturaDto.Response update(Integer id, ProfesorAsignaturaDto.Update dto) {
        ProfesorAsignaturaEntity entity = asignaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación con ID \"" + id + "\" no encontrada."));

        if (dto.getIdProfesor() != null) entity.setIdProfesor(dto.getIdProfesor());
        if (dto.getIdAsignatura() != null) entity.setIdAsignatura(dto.getIdAsignatura());
        if (dto.getIdPeriodo() != null) entity.setIdPeriodo(dto.getIdPeriodo());
        ProfesorAsignaturaEntity saved = asignaturaRepository.save(entity);

        if (dto.getIdGrupos() != null) {
            grupoRepository.deleteByIdProfesorAsignatura(id);
            guardarGrupos(id, dto.getIdGrupos());
        }

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void remove(Integer id) {
        if (!asignaturaRepository.existsById(id)) {
            throw new RuntimeException("Asignación con ID \"" + id + "\" no encontrada.");
        }
        grupoRepository.deleteByIdProfesorAsignatura(id);
        asignaturaRepository.deleteById(id);
    }

    private void guardarGrupos(Integer idProfesorAsignatura, List<Integer> idGrupos) {
        if (idGrupos == null) return;
        for (Integer idGrupo : idGrupos) {
            ProfesorAsignaturaGrupoEntity grupoEntity = new ProfesorAsignaturaGrupoEntity();
            grupoEntity.setIdProfesorAsignatura(idProfesorAsignatura);
            grupoEntity.setIdGrupo(idGrupo);
            grupoRepository.save(grupoEntity);
        }
    }

    private ProfesorAsignaturaDto.Response toResponse(ProfesorAsignaturaEntity entity) {
        ProfesorAsignaturaDto.Response response = new ProfesorAsignaturaDto.Response();
        response.setId(entity.getId());
        response.setIdProfesor(entity.getIdProfesor());
        response.setIdAsignatura(entity.getIdAsignatura());
        response.setIdPeriodo(entity.getIdPeriodo());

        List<Integer> idGrupos = grupoRepository.findByIdProfesorAsignatura(entity.getId())
                .stream()
                .map(ProfesorAsignaturaGrupoEntity::getIdGrupo)
                .collect(Collectors.toList());
        response.setIdGrupos(idGrupos);

        return response;
    }
}