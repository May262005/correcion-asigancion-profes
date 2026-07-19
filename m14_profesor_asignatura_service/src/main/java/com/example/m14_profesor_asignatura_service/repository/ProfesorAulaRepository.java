package com.example.m14_profesor_asignatura_service.repository;

import com.example.m14_profesor_asignatura_service.entity.ProfesorAulaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProfesorAulaRepository extends JpaRepository<ProfesorAulaEntity, Integer> {
    Optional<ProfesorAulaEntity> findByIdProfesorAndIdPeriodo(Integer idProfesor, Integer idPeriodo);
    List<ProfesorAulaEntity> findAll();
}