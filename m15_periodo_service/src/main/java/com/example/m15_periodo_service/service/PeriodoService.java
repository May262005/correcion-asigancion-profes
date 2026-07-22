package com.example.m15_periodo_service.service;

import com.example.m15_periodo_service.dto.PeriodoDto;
import com.example.m15_periodo_service.entity.PeriodoEntity;
import com.example.m15_periodo_service.repository.PeriodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PeriodoService {

    private final PeriodoRepository periodoRepository;

    public PeriodoService(PeriodoRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }

    @Transactional
    public PeriodoEntity create(PeriodoDto.Create dto) {
        PeriodoEntity entity = new PeriodoEntity();
        entity.setNombre(dto.getNombre());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        entity.setPrivilegioDefault(dto.getPrivilegioDefault() != null ? dto.getPrivilegioDefault() : "sin_privilegio");
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : false);
        return periodoRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<PeriodoEntity> findAll() {
        return periodoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PeriodoEntity findOne(Integer id) {
        return periodoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Periodo con ID \"" + id + "\" no encontrado."));
    }

    @Transactional
    public PeriodoEntity update(Integer id, PeriodoDto.Update dto) {
        PeriodoEntity entity = findOne(id);
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getFechaInicio() != null) entity.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) entity.setFechaFin(dto.getFechaFin());
        if (dto.getPrivilegioDefault() != null) entity.setPrivilegioDefault(dto.getPrivilegioDefault());
        if (dto.getActivo() != null) entity.setActivo(dto.getActivo());
        return periodoRepository.save(entity);
    }

    @Transactional
    public void remove(Integer id) {
        if (!periodoRepository.existsById(id)) {
            throw new RuntimeException("Periodo con ID \"" + id + "\" no encontrado.");
        }
        periodoRepository.deleteById(id);
    }
}