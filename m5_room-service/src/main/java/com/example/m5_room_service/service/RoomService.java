package com.example.m5_room_service.service;

// IMPORTS CORREGIDOS: Apuntando al paquete real de tu microservicio
import com.example.m5_room_service.dto.RoomDto;
import com.example.m5_room_service.entity.RoomEntity; // Usamos RoomEntity en lugar de Room
import java.util.List;

public interface RoomService {
    
    RoomEntity create(RoomDto.Create createRoomDto);
    
    List<RoomEntity> findAll();
    
    RoomEntity findOne(Long id);
    
    RoomEntity update(Long id, RoomDto.Update updateRoomDto);
    
    void remove(Long id);
}