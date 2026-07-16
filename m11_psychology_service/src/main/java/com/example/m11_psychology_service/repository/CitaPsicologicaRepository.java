package com.example.m11_psychology_service.repository;

import com.example.m11_psychology_service.entity.CitaPsicologicaEntity;
import com.example.m11_psychology_service.entity.CitaPsicologicaEntity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaPsicologicaRepository extends JpaRepository<CitaPsicologicaEntity, Long> {
    List<CitaPsicologicaEntity> findByIdEstudiante(Long idEstudiante);
    List<CitaPsicologicaEntity> findByIdProfesor(Long idProfesor);
    List<CitaPsicologicaEntity> findByEstado(EstadoCita estado);
}