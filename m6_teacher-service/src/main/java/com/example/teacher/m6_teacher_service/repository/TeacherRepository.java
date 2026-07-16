package com.example.teacher.m6_teacher_service.repository;

import com.example.teacher.m6_teacher_service.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    List<Teacher> findByEsPsicologo(Boolean esPsicologo);
}