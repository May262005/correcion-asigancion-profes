package com.example.teacher.m6_teacher_service.controller;

import com.example.teacher.m6_teacher_service.dto.TeacherDto;
import com.example.teacher.m6_teacher_service.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherService service;

    @GetMapping
    public ResponseEntity<List<TeacherDto>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDto> getById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TeacherDto> create(@RequestBody TeacherDto dto) {
        TeacherDto created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/teachers/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherDto> update(@PathVariable Integer id, @RequestBody TeacherDto dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (service.delete(id)) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}
