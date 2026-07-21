package com.example.m3_prifile_service.controller;

import com.example.m3_prifile_service.dto.AvatarDto;
import com.example.m3_prifile_service.dto.ProfileDto;
import com.example.m3_prifile_service.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controlador REST del microservicio m3_profile_service.
 *
 * Base URL: /api/v1/profiles
 *
 * ──────────────────────────────────────────────────────────────
 * ENDPOINTS DE PERFIL
 * ──────────────────────────────────────────────────────────────
 *  GET    /api/v1/profiles/{usuarioId}              → obtener perfil
 *  PUT    /api/v1/profiles/{usuarioId}              → actualizar datos personales + avatar
 *  PATCH  /api/v1/profiles/{usuarioId}/avatar/{avatarId} → cambiar solo el avatar
 *
 * ──────────────────────────────────────────────────────────────
 * ENDPOINTS DE AVATARES (catálogo)
 * ──────────────────────────────────────────────────────────────
 *  GET    /api/v1/profiles/avatares                 → listar todos
 *  GET    /api/v1/profiles/avatares/{id}            → obtener uno
 *  POST   /api/v1/profiles/avatares                 → crear
 *  PUT    /api/v1/profiles/avatares/{id}            → actualizar
 *  DELETE /api/v1/profiles/avatares/{id}            → eliminar
 */
@RestController
@RequestMapping("/api/v1/profiles")
//@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // ══════════════════════════════════════════════
    // PERFIL
    // ══════════════════════════════════════════════

    /**
     * Obtiene el perfil de un usuario.
     * GET /api/v1/profiles/{usuarioId}
     */
    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Integer usuarioId) {
        try {
            ProfileDto dto = profileService.obtenerPerfil(usuarioId);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(e.getMessage());
        }
    }

    /**
     * Actualiza nombre, apellidos y avatar de un usuario.
     * PUT /api/v1/profiles/{usuarioId}
     *
     * Body JSON ejemplo:
     * {
     *   "nombre": "Juan",
     *   "apellidoPaterno": "García",
     *   "apellidoMaterno": "López",
     *   "avatar": { "id": 3 }
     * }
     */
    @PutMapping("/{usuarioId}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Integer usuarioId,
                                              @Valid @RequestBody ProfileDto dto) {
        try {
            ProfileDto actualizado = profileService.actualizarPerfil(usuarioId, dto);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(e.getMessage());
        }
    }

    /**
     * Cambia únicamente el avatar del usuario.
     * PATCH /api/v1/profiles/{usuarioId}/avatar/{avatarId}
     */
    @PatchMapping("/{usuarioId}/avatar/{avatarId}")
    public ResponseEntity<?> cambiarAvatar(@PathVariable Integer usuarioId,
                                           @PathVariable Integer avatarId) {
        try {
            ProfileDto actualizado = profileService.cambiarAvatar(usuarioId, avatarId);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(e.getMessage());
        }
    }

    // ══════════════════════════════════════════════
    // CATÁLOGO DE AVATARES
    // ══════════════════════════════════════════════

    /** GET /api/v1/profiles/avatares */
    @GetMapping("/avatares")
    public ResponseEntity<List<AvatarDto>> listarAvatares() {
        return ResponseEntity.ok(profileService.listarAvatares());
    }

    /** GET /api/v1/profiles/avatares/{id} */
    @GetMapping("/avatares/{id}")
    public ResponseEntity<?> obtenerAvatar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(profileService.obtenerAvatar(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * POST /api/v1/profiles/avatares
     *
     * Body JSON:
     * {
     *   "imagen": "/assets/avatares/avatar1.png",
     *   "nombre": "Zorro Azul"
     * }
     */
    @PostMapping("/avatares")
    public ResponseEntity<AvatarDto> crearAvatar(@Valid @RequestBody AvatarDto dto) {
        AvatarDto creado = profileService.crearAvatar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /** PUT /api/v1/profiles/avatares/{id} */
    @PutMapping("/avatares/{id}")
    public ResponseEntity<?> actualizarAvatar(@PathVariable Integer id,
                                              @Valid @RequestBody AvatarDto dto) {
        try {
            return ResponseEntity.ok(profileService.actualizarAvatar(id, dto));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /** DELETE /api/v1/profiles/avatares/{id} */
    @DeleteMapping("/avatares/{id}")
    public ResponseEntity<?> eliminarAvatar(@PathVariable Integer id) {
        try {
            profileService.eliminarAvatar(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}