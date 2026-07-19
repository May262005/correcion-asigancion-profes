package com.example.m4_building_service.controller;

import com.example.m4_building_service.dto.BuildingDto;
import com.example.m4_building_service.entity.BuildingEntity;
import com.example.m4_building_service.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "buildings", description = "Endpoints para la gestión de edificios")
@RestController
@RequestMapping("/edificios")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BuildingEntity create(@Valid @RequestBody BuildingDto.Create createBuildingDto) {
        return buildingService.create(createBuildingDto);
    }

    @GetMapping
    @Operation(summary = "Obtiene el listado de todos los edificios.")
    public List<BuildingEntity> findAll() {
        return buildingService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un edificio por su ID.")
    public BuildingEntity findOne(@PathVariable Long id) {
        return buildingService.findOne(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualiza campos de un edificio existente.")
    public BuildingEntity update(
            @PathVariable Long id,
            @Valid @RequestBody BuildingDto.Update updateBuildingDto) {
        return buildingService.update(id, updateBuildingDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un edificio por su ID.")
    public void remove(@PathVariable Long id) {
        buildingService.remove(id);
    }
}