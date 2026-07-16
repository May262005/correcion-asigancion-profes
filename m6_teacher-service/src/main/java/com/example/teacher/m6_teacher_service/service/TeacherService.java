package com.example.teacher.m6_teacher_service.service;

import com.example.teacher.m6_teacher_service.dto.TeacherDto;
import com.example.teacher.m6_teacher_service.entity.Teacher;
import com.example.teacher.m6_teacher_service.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository repository;

    @Transactional(readOnly = true)
    public List<TeacherDto> findAll() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TeacherDto> findById(Integer id) {
        return repository.findById(id).map(this::toDto);
    }

    @Transactional
    public TeacherDto create(TeacherDto dto) {
        Teacher t = toEntity(dto);
        return toDto(repository.save(t));
    }

    @Transactional
    public Optional<TeacherDto> update(Integer id, TeacherDto dto) {
        return repository.findById(id).map(existing -> {
            existing.setAbreviaturaNombre(dto.getAbreviaturaNombre());
            existing.setTelefono(dto.getTelefono());
            existing.setTitulo(dto.getTitulo());
            existing.setColorCalendario(dto.getColorCalendario());
            existing.setEsPsicologo(dto.getEsPsicologo());
            return toDto(repository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private TeacherDto toDto(Teacher t) {
        TeacherDto d = new TeacherDto();
        d.setId(t.getId());
        d.setIdUsuario(t.getIdUsuario());
        d.setAbreviaturaNombre(t.getAbreviaturaNombre());
        d.setTelefono(t.getTelefono());
        d.setTitulo(t.getTitulo());
        d.setColorCalendario(t.getColorCalendario());
        d.setEsPsicologo(t.getEsPsicologo());
        return d;
    }

    private Teacher toEntity(TeacherDto d) {
        Teacher t = new Teacher();
        t.setIdUsuario(d.getIdUsuario());
        t.setAbreviaturaNombre(d.getAbreviaturaNombre());
        t.setTelefono(d.getTelefono());
        t.setTitulo(d.getTitulo());
        t.setColorCalendario(d.getColorCalendario());
        t.setEsPsicologo(d.getEsPsicologo());
        return t;
    }
}