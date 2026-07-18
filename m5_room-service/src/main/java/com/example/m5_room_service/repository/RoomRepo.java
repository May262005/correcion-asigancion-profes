package com.example.m5_room_service.repository;

import com.example.m5_room_service.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepo extends JpaRepository<RoomEntity, Long> {
    // ¡Listo! No necesitas agregar ningún método aquí adentro.
    // JpaRepository ya te da por defecto: save(), findById(), findAll(), deleteById(), etc.
}