package com.example.curriculum.m7_curriculum_service.service;

import com.example.curriculum.m7_curriculum_service.dto.CurriculumDto;
import com.example.curriculum.m7_curriculum_service.entity.CurriculumEntity;
import com.example.curriculum.m7_curriculum_service.repository.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CurriculumService {

    @Autowired
    private CurriculumRepository repository;

    @Transactional(readOnly = true)
    public List<CurriculumDto> findAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CurriculumDto> findById(Integer id) {
        return repository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<CurriculumDto> findByDivision(Integer divisionId) {
        return repository.findByDivisionId(divisionId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CurriculumDto create(CurriculumDto dto) {
        return toDto(repository.save(toEntity(dto)));
    }

    @Transactional
    public Optional<CurriculumDto> update(Integer id, CurriculumDto dto) {
        return repository.findById(id).map(existing -> {
            existing.setNombre(dto.getNombre());
            existing.setAbreviatura(dto.getAbreviatura());
            existing.setColorIdentificador(dto.getColorIdentificador());
            existing.setTipo(dto.getTipo());
            existing.setHorasSemanales(dto.getHorasSemanales());
            existing.setDuracionBloqueHorasMin(dto.getDuracionBloqueHorasMin());
            existing.setDuracionBloqueHorasMax(dto.getDuracionBloqueHorasMax());
            existing.setDivisionId(dto.getDivisionId());
            return toDto(repository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private CurriculumDto toDto(CurriculumEntity e) {
        CurriculumDto d = new CurriculumDto();
        d.setId(e.getId());
        d.setNombre(e.getNombre());
        d.setAbreviatura(e.getAbreviatura());
        d.setColorIdentificador(e.getColorIdentificador());
        d.setTipo(e.getTipo());
        d.setHorasSemanales(e.getHorasSemanales());
        d.setDuracionBloqueHorasMin(e.getDuracionBloqueHorasMin());
        d.setDuracionBloqueHorasMax(e.getDuracionBloqueHorasMax());
        d.setDivisionId(e.getDivisionId());
        return d;
    }

    private CurriculumEntity toEntity(CurriculumDto d) {
        CurriculumEntity e = new CurriculumEntity();
        e.setNombre(d.getNombre());
        e.setAbreviatura(d.getAbreviatura());
        e.setColorIdentificador(d.getColorIdentificador());
        e.setTipo(d.getTipo());
        e.setHorasSemanales(d.getHorasSemanales());
        e.setDuracionBloqueHorasMin(d.getDuracionBloqueHorasMin());
        e.setDuracionBloqueHorasMax(d.getDuracionBloqueHorasMax());
        e.setDivisionId(d.getDivisionId());
        return e;
    }
}