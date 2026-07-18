package com.example.m5_room_service.service;

// IMPORTS CORREGIDOS: Eliminamos el import de Building
import com.example.m5_room_service.dto.RoomDto;
import com.example.m5_room_service.entity.RoomEntity;
import com.example.m5_room_service.repository.RoomRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;

    // Constructor corregido usando el nombre correcto de la interfaz: RoomRepo
    public RoomServiceImpl(RoomRepo roomRepo) {
        this.roomRepo = roomRepo;
    }

    // --- C: CREATE ---
    @Override
    @Transactional
    public RoomEntity create(RoomDto.Create createRoomDto) {
        RoomEntity room = new RoomEntity();
        room.setNombre(createRoomDto.getNombre());
        room.setAbreviatura(createRoomDto.getAbreviatura());
        room.setUbicacion(createRoomDto.getUbicacion());
        room.setCapacidad(createRoomDto.getCapacidad());
        
        // Mapeo directo de microservicios: guardamos el ID numérico
        room.setIdEdificio(createRoomDto.getIdEdificio());

        return roomRepo.save(room);
    }

    // --- R: READ ALL ---
    @Override
    @Transactional(readOnly = true)
    public List<RoomEntity> findAll() {
        return roomRepo.findAll();
    }

    // --- R: READ ONE ---
    @Override
    @Transactional(readOnly = true)
    public RoomEntity findOne(Long id) {
        return roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula con ID \"" + id + "\" no encontrada."));
    }

    // --- U: UPDATE ---
    @Override
    @Transactional
    public RoomEntity update(Long id, RoomDto.Update updateRoomDto) {
        RoomEntity room = findOne(id); 

        if (updateRoomDto.getNombre() != null) {
            room.setNombre(updateRoomDto.getNombre());
        }
        if (updateRoomDto.getAbreviatura() != null) {
            room.setAbreviatura(updateRoomDto.getAbreviatura());
        }
        if (updateRoomDto.getUbicacion() != null) {
            room.setUbicacion(updateRoomDto.getUbicacion());
        }
        if (updateRoomDto.getCapacidad() != null) {
            room.setCapacidad(updateRoomDto.getCapacidad());
        }
        if (updateRoomDto.getIdEdificio() != null) {
            // Mapeo selectivo del ID numérico si cambia
            room.setIdEdificio(updateRoomDto.getIdEdificio());
        }

        return roomRepo.save(room);
    }

    // --- D: DELETE ---
    @Override
    @Transactional
    public void remove(Long id) {
        if (!roomRepo.existsById(id)) {
            throw new RuntimeException("Aula con ID \"" + id + "\" no encontrada.");
        }
        roomRepo.deleteById(id);
    }
}