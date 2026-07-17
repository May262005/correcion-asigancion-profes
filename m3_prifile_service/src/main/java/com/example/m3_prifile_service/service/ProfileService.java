package com.example.m3_prifile_service.service;

import com.example.m3_prifile_service.dto.AvatarDto;
import com.example.m3_prifile_service.dto.ProfileDto;
import com.example.m3_prifile_service.entity.AvatarEntity;
import com.example.m3_prifile_service.entity.UsuarioProfileEntity;
import com.example.m3_prifile_service.repository.AvatarRepository;
import com.example.m3_prifile_service.repository.UsuarioProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Servicio principal de m3_profile_service.
 *
 * Responsabilidades:
 *  1. CRUD de avatares (catálogo de imágenes disponibles).
 *  2. Consulta y actualización de datos de perfil de un usuario.
 *  3. Cambio de avatar asociado al usuario.
 */
@Service
public class ProfileService {

    private final UsuarioProfileRepository usuarioRepo;
    private final AvatarRepository         avatarRepo;

    public ProfileService(UsuarioProfileRepository usuarioRepo,
                          AvatarRepository         avatarRepo) {
        this.usuarioRepo = usuarioRepo;
        this.avatarRepo  = avatarRepo;
    }

    // ══════════════════════════════════════════════
    // AVATARES — catálogo
    // ══════════════════════════════════════════════

    /** Lista todos los avatares disponibles */
    public List<AvatarDto> listarAvatares() {
        return avatarRepo.findAll()
                         .stream()
                         .map(this::toAvatarDto)
                         .collect(Collectors.toList());
    }

    /** Obtiene un avatar por su ID */
    public AvatarDto obtenerAvatar(Integer id) {
        AvatarEntity entity = avatarRepo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Avatar no encontrado con id: " + id));
        return toAvatarDto(entity);
    }

    /** Crea un nuevo avatar en el catálogo */
    @Transactional
    public AvatarDto crearAvatar(AvatarDto dto) {
        AvatarEntity entity = new AvatarEntity(dto.getImagen(), dto.getNombre());
        AvatarEntity saved  = avatarRepo.save(entity);
        return toAvatarDto(saved);
    }

    /** Actualiza imagen y/o nombre de un avatar existente */
    @Transactional
    public AvatarDto actualizarAvatar(Integer id, AvatarDto dto) {
        AvatarEntity entity = avatarRepo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Avatar no encontrado con id: " + id));
        entity.setImagen(dto.getImagen());
        entity.setNombre(dto.getNombre());
        return toAvatarDto(avatarRepo.save(entity));
    }

    /** Elimina un avatar del catálogo */
    @Transactional
    public void eliminarAvatar(Integer id) {
        if (!avatarRepo.existsById(id)) {
            throw new NoSuchElementException("Avatar no encontrado con id: " + id);
        }
        avatarRepo.deleteById(id);
    }

    // ══════════════════════════════════════════════
    // PERFIL DE USUARIO
    // ══════════════════════════════════════════════

    /** Obtiene el perfil completo de un usuario */
    public ProfileDto obtenerPerfil(Integer usuarioId) {
        UsuarioProfileEntity entity = usuarioRepo.findById(usuarioId)
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con id: " + usuarioId));
        return toProfileDto(entity);
    }

    /**
     * Actualiza los datos personales del perfil:
     * nombre, apellidos y avatar (por su ID).
     * El correo y el rol NO se modifican aquí.
     */
    @Transactional
    public ProfileDto actualizarPerfil(Integer usuarioId, ProfileDto dto) {
        UsuarioProfileEntity entity = usuarioRepo.findById(usuarioId)
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con id: " + usuarioId));

        entity.setNombre(dto.getNombre());
        entity.setApellidoPaterno(dto.getApellidoPaterno());
        entity.setApellidoMaterno(dto.getApellidoMaterno());

        // Cambio de avatar si viene en el DTO
        if (dto.getAvatar() != null && dto.getAvatar().getId() != null) {
            AvatarEntity avatar = avatarRepo.findById(dto.getAvatar().getId())
                .orElseThrow(() -> new NoSuchElementException(
                    "Avatar no encontrado con id: " + dto.getAvatar().getId()));
            entity.setAvatar(avatar);
        }

        return toProfileDto(usuarioRepo.save(entity));
    }

    /**
     * Cambia solo el avatar de un usuario.
     * Endpoint liviano: PATCH /perfiles/{usuarioId}/avatar/{avatarId}
     */
    @Transactional
    public ProfileDto cambiarAvatar(Integer usuarioId, Integer avatarId) {
        // Verificamos existencia antes de la actualización directa
        if (!usuarioRepo.existsById(usuarioId)) {
            throw new NoSuchElementException("Usuario no encontrado con id: " + usuarioId);
        }
        if (!avatarRepo.existsById(avatarId)) {
            throw new NoSuchElementException("Avatar no encontrado con id: " + avatarId);
        }
        usuarioRepo.actualizarAvatar(usuarioId, avatarId);
        // Recargamos para devolver el estado actualizado
        return obtenerPerfil(usuarioId);
    }

    // ══════════════════════════════════════════════
    // Mappers internos
    // ══════════════════════════════════════════════

    private AvatarDto toAvatarDto(AvatarEntity e) {
        return new AvatarDto(e.getId(), e.getImagen(), e.getNombre());
    }

    private ProfileDto toProfileDto(UsuarioProfileEntity e) {
        ProfileDto dto = new ProfileDto();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setApellidoPaterno(e.getApellidoPaterno());
        dto.setApellidoMaterno(e.getApellidoMaterno());
        dto.setCorreoElectronico(e.getCorreoElectronico());
        dto.setRol(e.getRol() != null ? e.getRol().name() : null);
        dto.setFechaRegistro(e.getFechaRegistro() != null
                             ? e.getFechaRegistro().toString() : null);
        if (e.getAvatar() != null) {
            dto.setAvatar(toAvatarDto(e.getAvatar()));
        }
        return dto;
    }
}