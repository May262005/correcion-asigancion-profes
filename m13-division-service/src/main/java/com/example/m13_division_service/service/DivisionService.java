package com.example.m13_division_service.service;

import com.example.m13_division_service.dto.DivisionDto;
import com.example.m13_division_service.entity.Division;
import com.example.m13_division_service.repository.DivisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DivisionService {

    private final DivisionRepository divisionRepository;

    public DivisionService(DivisionRepository divisionRepository) {
        this.divisionRepository = divisionRepository;
    }

    @Transactional
    public DivisionDto.Response create(DivisionDto.Create dto) {
        // Verificar si ya existe una división con el mismo nombre
        if (divisionRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Ya existe una división con el nombre: " + dto.getNombre());
        }

        Division division = new Division();
        division.setNombre(dto.getNombre());
        division.setAbreviatura(dto.getAbreviatura());

        Division saved = divisionRepository.save(division);
        return DivisionDto.Response.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<DivisionDto.Response> findAll() {
        return divisionRepository.findAll().stream()
                .map(DivisionDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DivisionDto.Response findOne(Long id) { // Cambiado a Long
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("División no encontrada con ID: " + id));
        return DivisionDto.Response.fromEntity(division);
    }

    @Transactional
    public DivisionDto.Response update(Long id, DivisionDto.Update dto) { // Cambiado a Long
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("División no encontrada con ID: " + id));

        // Verificar si el nuevo nombre ya existe (si se está cambiando)
        if (dto.getNombre() != null && !dto.getNombre().equals(division.getNombre())) {
            if (divisionRepository.existsByNombre(dto.getNombre())) {
                throw new RuntimeException("Ya existe una división con el nombre: " + dto.getNombre());
            }
            division.setNombre(dto.getNombre());
        }

        if (dto.getAbreviatura() != null) {
            division.setAbreviatura(dto.getAbreviatura());
        }

        Division updated = divisionRepository.save(division);
        return DivisionDto.Response.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id) { // Cambiado a Long
        if (!divisionRepository.existsById(id)) {
            throw new RuntimeException("División no encontrada con ID: " + id);
        }
        divisionRepository.deleteById(id);
    }
}