package com.example.m10_turno_service.service;

import com.example.m10_turno_service.dto.TurnoDto;
import com.example.m10_turno_service.entity.TurnoEntity;
import java.util.List;

public interface TurnoService {
    TurnoEntity create(TurnoDto.Create dto);
    List<TurnoEntity> findAll();
    TurnoEntity findOne(Integer id);
    TurnoEntity update(Integer id, TurnoDto.Update dto);
    void remove(Integer id);
}