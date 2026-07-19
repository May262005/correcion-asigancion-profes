package com.example.m14_profesor_asignatura_service.service;

import com.example.m14_profesor_asignatura_service.dto.ProfesorAulaDto;
import com.example.m14_profesor_asignatura_service.entity.ProfesorAulaEntity;
import java.util.List;

public interface ProfesorAulaService {
    ProfesorAulaEntity create(ProfesorAulaDto.Create dto);
    List<ProfesorAulaEntity> findAll();
    ProfesorAulaEntity findOne(Integer id);
    ProfesorAulaEntity update(Integer id, ProfesorAulaDto.Update dto);
    void remove(Integer id);
}