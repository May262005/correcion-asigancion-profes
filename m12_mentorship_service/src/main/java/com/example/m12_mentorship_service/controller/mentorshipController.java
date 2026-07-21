package com.example.m12_mentorship_service.controller;

import com.example.m12_mentorship_service.dto.AsesoriaDto;
import com.example.m12_mentorship_service.entity.AsesoriaEntity.EstadoAsesoria;
import com.example.m12_mentorship_service.service.mentorshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mentorship/asesorias")
@RequiredArgsConstructor
public class mentorshipController {

    private final mentorshipService mentorshipService;

    @PostMapping
    public ResponseEntity<AsesoriaDto> create(@RequestBody AsesoriaDto dto) {
        return ResponseEntity.ok(mentorshipService.createAsesoria(dto));
    }

    @GetMapping
    public ResponseEntity<List<AsesoriaDto>> getAll() {
        return ResponseEntity.ok(mentorshipService.getAllAsesorias());
    }

    @GetMapping("/horarios-disponibles")
    public ResponseEntity<List<String>> getHorariosDisponibles(
            @RequestParam Long idProfesor,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(mentorshipService.getHorariosDisponibles(idProfesor, fecha));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsesoriaDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mentorshipService.getAsesoriaById(id));
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<AsesoriaDto>> getByEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(mentorshipService.getAsesoriasByEstudiante(idEstudiante));
    }

    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<List<AsesoriaDto>> getByProfesor(@PathVariable Long idProfesor) {
        return ResponseEntity.ok(mentorshipService.getAsesoriasByProfesor(idProfesor));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<AsesoriaDto> updateEstado(@PathVariable Long id, @RequestParam EstadoAsesoria estado) {
        return ResponseEntity.ok(mentorshipService.updateEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mentorshipService.deleteAsesoria(id);
        return ResponseEntity.noContent().build();
    }
}