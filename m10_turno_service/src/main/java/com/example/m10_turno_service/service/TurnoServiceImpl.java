package com.example.m10_turno_service.service;

import com.example.m10_turno_service.dto.TurnoDto;
import com.example.m10_turno_service.entity.TurnoEntity;
import com.example.m10_turno_service.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoServiceImpl(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    @Override
    @Transactional
    public TurnoEntity create(TurnoDto.Create dto) {
        TurnoEntity turno = new TurnoEntity();
        turno.setNombre(dto.getNombre());
        turno.setDiaInicio(dto.getDiaInicio());
        turno.setDiaFin(dto.getDiaFin());
        turno.setHoraInicio(dto.getHoraInicio());
        turno.setHoraFin(dto.getHoraFin());
        return turnoRepository.save(turno);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoEntity> findAll() {
        return turnoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TurnoEntity findOne(Integer id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno con ID \"" + id + "\" no encontrado."));
    }

    @Override
    @Transactional
    public TurnoEntity update(Integer id, TurnoDto.Update dto) {
        TurnoEntity turno = findOne(id);
        if (dto.getNombre() != null) turno.setNombre(dto.getNombre());
        if (dto.getDiaInicio() != null) turno.setDiaInicio(dto.getDiaInicio());
        if (dto.getDiaFin() != null) turno.setDiaFin(dto.getDiaFin());
        if (dto.getHoraInicio() != null) turno.setHoraInicio(dto.getHoraInicio());
        if (dto.getHoraFin() != null) turno.setHoraFin(dto.getHoraFin());
        return turnoRepository.save(turno);
    }

    @Override
    @Transactional
    public void remove(Integer id) {
        if (!turnoRepository.existsById(id)) {
            throw new RuntimeException("Turno con ID \"" + id + "\" no encontrado.");
        }
        turnoRepository.deleteById(id);
    }
}