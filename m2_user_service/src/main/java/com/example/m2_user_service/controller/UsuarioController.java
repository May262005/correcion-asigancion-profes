package com.example.m2_user_service.controller;

import com.example.m2_user_service.dto.UsuarioDTOs.*;
import com.example.m2_user_service.dto.UsuarioRolDTO;
import com.example.m2_user_service.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/perfil/{id}")
    public ResponseEntity<PerfilCompletoDTO> obtenerPerfil(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(id));
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarPerfil(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarPerfilDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, dto));
    }

    // ✅ NUEVO ENDPOINT: Obtener usuario con idRol
    @GetMapping("/rol/{id}")
    public ResponseEntity<UsuarioRolDTO> getUsuarioRol(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.getUsuarioRol(id));
    }
}