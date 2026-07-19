package com.example.m14_profesor_asignatura_service.service;

import com.example.m14_profesor_asignatura_service.dto.ProfesorAulaDto;
import com.example.m14_profesor_asignatura_service.entity.ProfesorAulaEntity;
import com.example.m14_profesor_asignatura_service.repository.ProfesorAulaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProfesorAulaServiceImpl implements ProfesorAulaService {

    private final ProfesorAulaRepository profesorAulaRepository;

    public ProfesorAulaServiceImpl(ProfesorAulaRepository profesorAulaRepository) {
        this.profesorAulaRepository = profesorAulaRepository;
    }

    @Override
    @Transactional
    public ProfesorAulaEntity create(ProfesorAulaDto.Create dto) {
        ProfesorAulaEntity entity = new ProfesorAulaEntity();
        entity.setIdProfesor(dto.getIdProfesor());
        entity.setIdAula(dto.getIdAula());
        entity.setIdPeriodo(dto.getIdPeriodo());
        return profesorAulaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesorAulaEntity> findAll() {
        return profesorAulaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ProfesorAulaEntity findOne(Integer id) {
        return profesorAulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación de aula con ID \"" + id + "\" no encontrada."));
    }

    @Override
    @Transactional
    public ProfesorAulaEntity update(Integer id, ProfesorAulaDto.Update dto) {
        ProfesorAulaEntity entity = findOne(id);
        if (dto.getIdProfesor() != null) entity.setIdProfesor(dto.getIdProfesor());
        if (dto.getIdAula() != null) entity.setIdAula(dto.getIdAula());
        if (dto.getIdPeriodo() != null) entity.setIdPeriodo(dto.getIdPeriodo());
        return profesorAulaRepository.save(entity);
    }

    @Override
    @Transactional
    public void remove(Integer id) {
        if (!profesorAulaRepository.existsById(id)) {
            throw new RuntimeException("Asignación de aula con ID \"" + id + "\" no encontrada.");
        }
        profesorAulaRepository.deleteById(id);
    }
}