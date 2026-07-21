package com.example.m5_room_service.controller;

import com.example.m5_room_service.dto.RoomDto;
import com.example.m5_room_service.dto.RoomDTOList;
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

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una nueva aula.")
    @ApiResponse(responseCode = "201", description = "Aula creada con éxito.")
    public RoomDto create(@Valid @RequestBody RoomDto.Create createRoomDto) {
        return roomService.create(createRoomDto);
    }

    @GetMapping
    @Operation(summary = "Obtiene el listado de todas las aulas enriquecido con el edificio.")
    @ApiResponse(responseCode = "200", description = "Listado de aulas.")
    public List<RoomDTOList> findAll() {
        return roomService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un aula por su ID.")
    @ApiResponse(responseCode = "200", description = "Aula encontrada.")
    @ApiResponse(responseCode = "404", description = "Aula no encontrada.")
    public RoomDto findOne(@PathVariable Long id) {
        return roomService.findOne(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualiza campos de un aula existente.")
    @ApiResponse(responseCode = "200", description = "Aula actualizada con éxito.")
    @ApiResponse(responseCode = "404", description = "Aula no encontrada.")
    public RoomDto update(
            @PathVariable Long id, 
            @Valid @RequestBody RoomDto.Update updateRoomDto
    ) {
        return roomService.update(id, updateRoomDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un aula por su ID.")
    @ApiResponse(responseCode = "204", description = "Aula eliminada con éxito.")
    @ApiResponse(responseCode = "404", description = "Aula no encontrada.")
    public void remove(@PathVariable Long id) {
        roomService.remove(id);
    }
}