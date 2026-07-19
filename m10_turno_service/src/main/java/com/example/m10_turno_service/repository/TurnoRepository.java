package com.example.m10_turno_service.repository;

import com.example.m10_turno_service.entity.TurnoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoRepository extends JpaRepository<TurnoEntity, Integer> {
}