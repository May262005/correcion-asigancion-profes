package com.example.m14_profesor_asignatura_service.repository;

import com.example.m14_profesor_asignatura_service.entity.ProfesorAsignaturaGrupoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfesorAsignaturaGrupoRepository extends JpaRepository<ProfesorAsignaturaGrupoEntity, Integer> {
    List<ProfesorAsignaturaGrupoEntity> findByIdProfesorAsignatura(Integer idProfesorAsignatura);
    void deleteByIdProfesorAsignatura(Integer idProfesorAsignatura);
}