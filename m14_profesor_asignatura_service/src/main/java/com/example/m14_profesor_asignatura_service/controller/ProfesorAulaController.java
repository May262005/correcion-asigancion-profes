package com.example.m14_profesor_asignatura_service.controller;

import com.example.m14_profesor_asignatura_service.dto.ProfesorAulaDto;
import com.example.m14_profesor_asignatura_service.entity.ProfesorAulaEntity;
import com.example.m14_profesor_asignatura_service.service.ProfesorAulaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profesor-aula")
public class ProfesorAulaController {

    private final ProfesorAulaService service;

    public ProfesorAulaController(ProfesorAulaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfesorAulaEntity create(@Valid @RequestBody ProfesorAulaDto.Create dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<ProfesorAulaEntity> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProfesorAulaEntity findOne(@PathVariable Integer id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public ProfesorAulaEntity update(@PathVariable Integer id, @RequestBody ProfesorAulaDto.Update dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Integer id) {
        service.remove(id);
    }
}