package com.example.m2_user_service.repository;

import com.example.m2_user_service.entity.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProfesorRepository extends JpaRepository<Profesor, Integer> {
    List<Profesor> findByEsPsicologoTrue();
    Optional<Profesor> findByIdUsuario(Integer idUsuario);
}