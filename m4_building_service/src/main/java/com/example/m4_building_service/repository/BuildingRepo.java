package com.example.m4_building_service.repository;

import com.example.m4_building_service.entity.BuildingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepo extends JpaRepository<BuildingEntity, Long> {
}