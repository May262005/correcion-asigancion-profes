package com.example.m15_periodo_service.service;

import com.example.m15_periodo_service.dto.PeriodoDto;
import com.example.m15_periodo_service.entity.PeriodoEntity;
import java.util.List;

public interface PeriodoService {
    PeriodoEntity create(PeriodoDto.Create dto);
    List<PeriodoEntity> findAll();
    PeriodoEntity findOne(Integer id);
    PeriodoEntity update(Integer id, PeriodoDto.Update dto);
    void remove(Integer id);
}