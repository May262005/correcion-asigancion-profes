package com.example.m5_room_service.controller;

// IMPORTS CORREGIDOS: Apuntando al paquete real de tu microservicio m5_room_service
import com.example.m5_room_service.dto.RoomDto;
import com.example.m5_room_service.entity.RoomEntity;
import com.example.m5_room_service.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "aulas", description = "Endpoints para la gestión de aulas y espacios educativos")
@RestController
@RequestMapping("/aulas") 
public class RoomController {

    private final RoomService roomService;

    // Inyección de dependencias por constructor
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // --- C: CREATE (POST /aulas) ---
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Código HTTP 201 Created
    @Operation(summary = "Crea una nueva aula.")
    @ApiResponse(responseCode = "201", description = "Aula creada con éxito.")
    public RoomEntity create(@Valid @RequestBody RoomDto.Create createRoomDto) {
        return roomService.create(createRoomDto);
    }

    // --- R: READ (GET /aulas) ---
    @GetMapping
    @Operation(summary = "Obtiene el listado de todas las aulas.")
    @ApiResponse(responseCode = "200", description = "Listado de aulas.")
    public List<RoomEntity> findAll() {
        return roomService.findAll();
    }

    // --- R: READ (GET /aulas/:id) ---
    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un aula por su ID.")
    @ApiResponse(responseCode = "200", description = "Aula encontrada.")
    @ApiResponse(responseCode = "404", description = "Aula no encontrada.")
    public RoomEntity findOne(@PathVariable Long id) {
        return roomService.findOne(id);
    }

    // --- U: UPDATE (PATCH /aulas/:id) ---
    @PatchMapping("/{id}")
    @Operation(summary = "Actualiza campos de un aula existente.")
    @ApiResponse(responseCode = "200", description = "Aula actualizada con éxito.")
    @ApiResponse(responseCode = "404", description = "Aula no encontrada.")
    public RoomEntity update(
            @PathVariable Long id, 
            @Valid @RequestBody RoomDto.Update updateRoomDto
    ) {
        return roomService.update(id, updateRoomDto);
    }

    // --- D: DELETE (DELETE /aulas/:id) ---
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Código HTTP 204 No Content
    @Operation(summary = "Elimina un aula por su ID.")
    @ApiResponse(responseCode = "204", description = "Aula eliminada con éxito.")
    @ApiResponse(responseCode = "404", description = "Aula no encontrada.")
    public void remove(@PathVariable Long id) {
        roomService.remove(id);
    }
}