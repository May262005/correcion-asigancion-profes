package com.example.curriculum.m7_curriculum_service.controller;

import com.example.curriculum.m7_curriculum_service.dto.CurriculumDto;
import com.example.curriculum.m7_curriculum_service.service.CurriculumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/curriculum")
public class CurriculumController {

    @Autowired
    private CurriculumService service;

    @GetMapping
    public ResponseEntity<List<CurriculumDto>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurriculumDto> getById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/division/{divisionId}")
    public ResponseEntity<List<CurriculumDto>> getByDivision(@PathVariable Integer divisionId) {
        return ResponseEntity.ok(service.findByDivision(divisionId));
    }

    @PostMapping
    public ResponseEntity<CurriculumDto> create(@RequestBody CurriculumDto dto) {
        CurriculumDto created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/curriculum/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CurriculumDto> update(@PathVariable Integer id, @RequestBody CurriculumDto dto) {
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