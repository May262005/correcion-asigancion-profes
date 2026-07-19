package com.example.m15_periodo_service.repository;

import com.example.m15_periodo_service.entity.PeriodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodoRepository extends JpaRepository<PeriodoEntity, Integer> {
}