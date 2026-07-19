package com.example.m11_psychology_service.controller;

import com.example.m11_psychology_service.dto.CitaPsicologicaDto;
import com.example.m11_psychology_service.entity.CitaPsicologicaEntity.EstadoCita;
import com.example.m11_psychology_service.service.psychologyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/psychology/citas")
@RequiredArgsConstructor
public class psychologyController {

    private final psychologyService psychologyService;

    @PostMapping
    public ResponseEntity<CitaPsicologicaDto> create(@RequestBody CitaPsicologicaDto dto) {
        return ResponseEntity.ok(psychologyService.createCita(dto));
    }

    @GetMapping
    public ResponseEntity<List<CitaPsicologicaDto>> getAll() {
        return ResponseEntity.ok(psychologyService.getAllCitas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaPsicologicaDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(psychologyService.getCitaById(id));
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<CitaPsicologicaDto>> getByEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(psychologyService.getCitasByEstudiante(idEstudiante));
    }

    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<List<CitaPsicologicaDto>> getByProfesor(@PathVariable Long idProfesor) {
        return ResponseEntity.ok(psychologyService.getCitasByProfesor(idProfesor));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CitaPsicologicaDto> updateEstado(@PathVariable Long id, @RequestParam EstadoCita estado) {
        return ResponseEntity.ok(psychologyService.updateEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        psychologyService.deleteCita(id);
        return ResponseEntity.noContent().build();
    }
}