package com.example.m2_user_service.controller;

import com.example.m2_user_service.dto.EstudianteDTOs.*;
import com.example.m2_user_service.service.EstudianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {
    private final EstudianteService estudianteService;

    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> create(@Valid @RequestBody CreateEstudianteDTO dto) {
        return ResponseEntity.ok(estudianteService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<EstudianteResponseDTO>> findAll() {
        return ResponseEntity.ok(estudianteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> findOne(@PathVariable Integer id) {
        return ResponseEntity.ok(estudianteService.findOne(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateEstudianteDTO dto) {
        return ResponseEntity.ok(estudianteService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Integer id) {
        estudianteService.remove(id);
        return ResponseEntity.noContent().build();
    }
}