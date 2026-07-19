package com.example.m15_periodo_service.service;

import com.example.m15_periodo_service.dto.PeriodoDto;
import com.example.m15_periodo_service.entity.PeriodoEntity;
import com.example.m15_periodo_service.repository.PeriodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PeriodoServiceImpl implements PeriodoService {

    private final PeriodoRepository periodoRepository;

    public PeriodoServiceImpl(PeriodoRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }

    @Override
    @Transactional
    public PeriodoEntity create(PeriodoDto.Create dto) {
        PeriodoEntity entity = new PeriodoEntity();
        entity.setNombre(dto.getNombre());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : false);
        return periodoRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodoEntity> findAll() {
        return periodoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodoEntity findOne(Integer id) {
        return periodoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Periodo con ID \"" + id + "\" no encontrado."));
    }

    @Override
    @Transactional
    public PeriodoEntity update(Integer id, PeriodoDto.Update dto) {
        PeriodoEntity entity = findOne(id);
        if (dto.getNombre() != null) entity.setNombre(dto.getNombre());
        if (dto.getFechaInicio() != null) entity.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) entity.setFechaFin(dto.getFechaFin());
        if (dto.getActivo() != null) entity.setActivo(dto.getActivo());
        return periodoRepository.save(entity);
    }

    @Override
    @Transactional
    public void remove(Integer id) {
        if (!periodoRepository.existsById(id)) {
            throw new RuntimeException("Periodo con ID \"" + id + "\" no encontrado.");
        }
        periodoRepository.deleteById(id);
    }
}
