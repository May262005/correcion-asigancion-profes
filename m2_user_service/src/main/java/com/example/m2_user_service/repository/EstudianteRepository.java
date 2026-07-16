package com.example.m2_user_service.repository;

import com.example.m2_user_service.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {
    Optional<Estudiante> findByIdUsuario(Integer idUsuario);
}