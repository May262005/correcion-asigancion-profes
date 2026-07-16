package com.example.m2_user_service.controller;

import com.example.m2_user_service.dto.ProfesorDTOs.*;
import com.example.m2_user_service.service.ProfesorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesores")
@RequiredArgsConstructor
public class ProfesorController {
    private final ProfesorService profesorService;

    @GetMapping("/psicologos")
    public ResponseEntity<List<ProfesorResponseDTO>> findAllPsicologos() {
        return ResponseEntity.ok(profesorService.findAllPsicologos());
    }

    @PostMapping
    public ResponseEntity<ProfesorResponseDTO> create(@Valid @RequestBody CreateProfesorDTO dto) {
        return ResponseEntity.ok(profesorService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProfesorResponseDTO>> findAll() {
        return ResponseEntity.ok(profesorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfesorResponseDTO> findOne(@PathVariable Integer id) {
        return ResponseEntity.ok(profesorService.findOne(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfesorResponseDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProfesorDTO dto) {
        return ResponseEntity.ok(profesorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Integer id) {
        profesorService.remove(id);
        return ResponseEntity.noContent().build();
    }
}