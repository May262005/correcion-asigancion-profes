package com.example.m10_turno_service.controller;

import com.example.m10_turno_service.dto.TurnoDto;
import com.example.m10_turno_service.entity.TurnoEntity;
import com.example.m10_turno_service.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TurnoEntity create(@Valid @RequestBody TurnoDto.Create dto) {
        return turnoService.create(dto);
    }

    @GetMapping
    public List<TurnoEntity> findAll() {
        return turnoService.findAll();
    }

    @GetMapping("/{id}")
    public TurnoEntity findOne(@PathVariable Integer id) {
        return turnoService.findOne(id);
    }

    @PatchMapping("/{id}")
    public TurnoEntity update(@PathVariable Integer id, @RequestBody TurnoDto.Update dto) {
        return turnoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Integer id) {
        turnoService.remove(id);
    }
}