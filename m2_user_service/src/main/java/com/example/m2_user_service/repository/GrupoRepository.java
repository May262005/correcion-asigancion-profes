package com.example.m2_user_service.repository;

import com.example.m2_user_service.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
    Optional<Grupo> findByNombre(String nombre);
}