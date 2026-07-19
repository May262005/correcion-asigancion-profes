package com.example.m14_profesor_asignatura_service.service;

import com.example.m14_profesor_asignatura_service.dto.ProfesorAsignaturaDto;
import java.util.List;

public interface ProfesorAsignaturaService {
    ProfesorAsignaturaDto.Response create(ProfesorAsignaturaDto.Create dto);
    List<ProfesorAsignaturaDto.Response> findAll();
    ProfesorAsignaturaDto.Response findOne(Integer id);
    ProfesorAsignaturaDto.Response update(Integer id, ProfesorAsignaturaDto.Update dto);
    void remove(Integer id);
}