package com.example.m14_profesor_asignatura_service.controller;

import com.example.m14_profesor_asignatura_service.dto.ProfesorAsignaturaDto;
import com.example.m14_profesor_asignatura_service.service.ProfesorAsignaturaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profesor-asignatura")
public class ProfesorAsignaturaController {

    private final ProfesorAsignaturaService service;

    public ProfesorAsignaturaController(ProfesorAsignaturaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfesorAsignaturaDto.Response create(@Valid @RequestBody ProfesorAsignaturaDto.Create dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<ProfesorAsignaturaDto.Response> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProfesorAsignaturaDto.Response findOne(@PathVariable Integer id) {
        return service.findOne(id);
    }

    @PatchMapping("/{id}")
    public ProfesorAsignaturaDto.Response update(@PathVariable Integer id, @RequestBody ProfesorAsignaturaDto.Update dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Integer id) {
        service.remove(id);
    }
}