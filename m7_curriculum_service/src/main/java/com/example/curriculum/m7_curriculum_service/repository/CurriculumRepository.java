package com.example.curriculum.m7_curriculum_service.repository;

import com.example.curriculum.m7_curriculum_service.entity.CurriculumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CurriculumRepository extends JpaRepository<CurriculumEntity, Integer> {
    List<CurriculumEntity> findByDivisionId(Integer divisionId);
}
