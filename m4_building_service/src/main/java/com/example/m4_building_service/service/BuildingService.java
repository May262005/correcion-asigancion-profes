package com.example.m4_building_service.service;

import com.example.m4_building_service.client.ClientFeing;
import com.example.m4_building_service.dto.BuildingDto;
import com.example.m4_building_service.dto.BuildingDTOList;
import com.example.m4_building_service.dto.DivisionDTO;
import com.example.m4_building_service.entity.BuildingEntity;
import com.example.m4_building_service.repository.BuildingRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BuildingService {

    private final BuildingRepo buildingRepo;
    private final ClientFeing clientFeing;

    public BuildingService(BuildingRepo buildingRepo, ClientFeing clientFeing) {
        this.buildingRepo = buildingRepo;
        this.clientFeing = clientFeing;
    }

    @Transactional
    public BuildingDto create(BuildingDto.Create createDto) {
        BuildingEntity entity = new BuildingEntity();
        entity.setNombre(createDto.getNombre());
        entity.setAbreviatura(createDto.getAbreviatura());
        entity.setTipo(createDto.getTipo());
        entity.setIdDivision(createDto.getIdDivision());

        BuildingEntity saved = buildingRepo.save(entity);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<BuildingDTOList> findAll() {
        List<BuildingEntity> buildings = buildingRepo.findAll();

        List<Long> divisionIds = buildings.stream()
                .map(BuildingEntity::getIdDivision)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> divisionNombreMap = new HashMap<>();
        for (Long idDiv : divisionIds) {
            try {
                DivisionDTO division = clientFeing.getDivisionById(idDiv);
                if (division != null) {
                    divisionNombreMap.put(division.getId(), division.getNombre());
                }
            } catch (Exception e) {
                divisionNombreMap.put(idDiv, "División no disponible");
            }
        }

        return buildings.stream().map(building -> {
            BuildingDTOList dto = new BuildingDTOList();
            dto.setId(building.getId());
            dto.setNombre(building.getNombre());
            dto.setAbreviatura(building.getAbreviatura());
            dto.setTipo(building.getTipo());
            dto.setIdDivision(building.getIdDivision());
            dto.setNombreDivision(divisionNombreMap.getOrDefault(building.getIdDivision(), "Sin división"));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BuildingDto findOne(Long id) {
        BuildingEntity entity = buildingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Edificio no encontrado con ID: " + id));
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<BuildingDto> findAllByIDs(List<Long> ids) {
        return buildingRepo.findAllById(ids).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BuildingDto update(Long id, BuildingDto.Update updateDto) {
        BuildingEntity entity = buildingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Edificio no encontrado con ID: " + id));

        if (updateDto.getNombre() != null) entity.setNombre(updateDto.getNombre());
        if (updateDto.getAbreviatura() != null) entity.setAbreviatura(updateDto.getAbreviatura());
        if (updateDto.getTipo() != null) entity.setTipo(updateDto.getTipo());
        if (updateDto.getIdDivision() != null) entity.setIdDivision(updateDto.getIdDivision());

        BuildingEntity updated = buildingRepo.save(entity);
        return toDto(updated);
    }

    @Transactional
    public void remove(Long id) {
        if (!buildingRepo.existsById(id)) {
            throw new RuntimeException("Edificio no encontrado con ID: " + id);
        }
        try {
            buildingRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "No se puede eliminar el edificio porque contiene aulas asignadas a horarios activos."
            );
        }
    }

    private BuildingDto toDto(BuildingEntity entity) {
        BuildingDto dto = new BuildingDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setAbreviatura(entity.getAbreviatura());
        dto.setTipo(entity.getTipo());
        dto.setIdDivision(entity.getIdDivision());
        return dto;
    }
}