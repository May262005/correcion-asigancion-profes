package com.example.m13_division_service.repository;

import com.example.m13_division_service.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> { // Cambiado Integer por Long
    boolean existsByNombre(String nombre);
    boolean existsByAbreviatura(String abreviatura);
}