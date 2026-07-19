package com.example.m15_periodo_service.controller;

import com.example.m15_periodo_service.dto.PeriodoDto;
import com.example.m15_periodo_service.entity.PeriodoEntity;
import com.example.m15_periodo_service.service.PeriodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/periodos")
public class PeriodoController {

    private final PeriodoService periodoService;

    public PeriodoController(PeriodoService periodoService) {
        this.periodoService = periodoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PeriodoEntity create(@Valid @RequestBody PeriodoDto.Create dto) {
        return periodoService.create(dto);
    }

    @GetMapping
    public List<PeriodoEntity> findAll() {
        return periodoService.findAll();
    }

    @GetMapping("/{id}")
    public PeriodoEntity findOne(@PathVariable Integer id) {
        return periodoService.findOne(id);
    }

    @PatchMapping("/{id}")
    public PeriodoEntity update(@PathVariable Integer id, @RequestBody PeriodoDto.Update dto) {
        return periodoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Integer id) {
        periodoService.remove(id);
    }
}