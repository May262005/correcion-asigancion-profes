package com.example.m5_room_service.service;

import com.example.m5_room_service.client.ClientFeing;
import com.example.m5_room_service.dto.BuildingDTO;
import com.example.m5_room_service.dto.RoomDTOList;
import com.example.m5_room_service.dto.RoomDto;
import com.example.m5_room_service.entity.RoomEntity;
import com.example.m5_room_service.repository.RoomRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepo roomRepo;
    private final ClientFeing clientFeing;

    public RoomService(RoomRepo roomRepo, ClientFeing clientFeing) {
        this.roomRepo = roomRepo;
        this.clientFeing = clientFeing;
    }

    @Transactional
    public RoomDto create(RoomDto.Create createDto) {
        RoomEntity entity = new RoomEntity();
        entity.setNombre(createDto.getNombre());
        entity.setAbreviatura(createDto.getAbreviatura());
        entity.setUbicacion(createDto.getUbicacion());
        entity.setCapacidad(createDto.getCapacidad());
        entity.setIdEdificio(createDto.getIdEdificio());

        RoomEntity saved = roomRepo.save(entity);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<RoomDTOList> findAll() {
        List<RoomEntity> rooms = roomRepo.findAll();

        // Extraer los IDs de edificios únicos para consultarlos por Feign
        List<Long> buildingIds = rooms.stream()
                .map(RoomEntity::getIdEdificio)
                .distinct()
                .collect(Collectors.toList());

        // Mapa para almacenar temporalmente el nombre de cada edificio obtenido
        Map<Long, String> buildingNombreMap = new HashMap<>();
        for (Long idEdif : buildingIds) {
            try {
                BuildingDTO building = clientFeing.getBuildingById(idEdif);
                if (building != null) {
                    buildingNombreMap.put(building.getId(), building.getNombre());
                }
            } catch (Exception e) {
                buildingNombreMap.put(idEdif, "Edificio no disponible");
            }
        }

        // Mapear y enriquecer el resultado con el nombre del edificio
        return rooms.stream().map(room -> {
            RoomDTOList dto = new RoomDTOList();
            dto.setId(room.getId());
            dto.setNombre(room.getNombre());
            dto.setAbreviatura(room.getAbreviatura());
            dto.setUbicacion(room.getUbicacion());
            dto.setCapacidad(room.getCapacidad());
            dto.setNombreEdificio(buildingNombreMap.getOrDefault(room.getIdEdificio(), "Sin edificio"));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomDto findOne(Long id) {
        RoomEntity entity = roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula no encontrada con ID: " + id));
        return toDto(entity);
    }

    @Transactional
    public RoomDto update(Long id, RoomDto.Update updateDto) {
        RoomEntity entity = roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula no encontrada con ID: " + id));

        if (updateDto.getNombre() != null) entity.setNombre(updateDto.getNombre());
        if (updateDto.getAbreviatura() != null) entity.setAbreviatura(updateDto.getAbreviatura());
        if (updateDto.getUbicacion() != null) entity.setUbicacion(updateDto.getUbicacion());
        if (updateDto.getCapacidad() != null) entity.setCapacidad(updateDto.getCapacidad());
        if (updateDto.getIdEdificio() != null) entity.setIdEdificio(updateDto.getIdEdificio());

        RoomEntity updated = roomRepo.save(entity);
        return toDto(updated);
    }

    @Transactional
    public void remove(Long id) {
        if (!roomRepo.existsById(id)) {
            throw new RuntimeException("Aula no encontrada con ID: " + id);
        }
        roomRepo.deleteById(id);
    }

    private RoomDto toDto(RoomEntity entity) {
        RoomDto dto = new RoomDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setAbreviatura(entity.getAbreviatura());
        dto.setUbicacion(entity.getUbicacion());
        dto.setCapacidad(entity.getCapacidad());
        dto.setIdEdificio(entity.getIdEdificio());
        return dto;
    }
}