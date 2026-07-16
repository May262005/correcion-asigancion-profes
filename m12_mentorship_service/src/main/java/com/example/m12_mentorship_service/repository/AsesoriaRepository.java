package com.example.m12_mentorship_service.repository;

import com.example.m12_mentorship_service.entity.AsesoriaEntity;
import com.example.m12_mentorship_service.entity.AsesoriaEntity.EstadoAsesoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsesoriaRepository extends JpaRepository<AsesoriaEntity, Long> {
    List<AsesoriaEntity> findByIdEstudiante(Long idEstudiante);
    List<AsesoriaEntity> findByIdProfesor(Long idProfesor);
    List<AsesoriaEntity> findByEstado(EstadoAsesoria estado);
}