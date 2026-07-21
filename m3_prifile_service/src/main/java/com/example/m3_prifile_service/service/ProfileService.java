package com.example.m3_prifile_service.service;

import com.example.m3_prifile_service.client.*;
import com.example.m3_prifile_service.dto.AvatarDto;
import com.example.m3_prifile_service.dto.ProfileDto;
import com.example.m3_prifile_service.entity.AvatarEntity;
import com.example.m3_prifile_service.repository.AvatarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final UsuarioClient usuarioClient;
    private final AvatarRepository avatarRepository;

    public ProfileService(UsuarioClient usuarioClient, AvatarRepository avatarRepository) {
        this.usuarioClient = usuarioClient;
        this.avatarRepository = avatarRepository;
    }

    public ProfileDto obtenerPerfil(Integer usuarioId) {
        PerfilCompletoResponse resp = usuarioClient.obtenerPerfil(usuarioId);
        if (resp == null || resp.getUsuario() == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo obtener el perfil: servicio de usuarios no disponible");
        }
        return construirProfileDto(resp.getUsuario());
    }

    public ProfileDto actualizarPerfil(Integer usuarioId, ProfileDto dto) {
        Integer idAvatarSolicitado = dto.getAvatar() != null ? dto.getAvatar().getId() : null;

        // Validación local: si piden cambiar de avatar, el avatar debe existir en NUESTRA tabla
        if (idAvatarSolicitado != null && !avatarRepository.existsById(idAvatarSolicitado)) {
            throw new NoSuchElementException("El avatar solicitado no existe: " + idAvatarSolicitado);
        }

        ActualizarPerfilRequest req = new ActualizarPerfilRequest();
        req.setNombre(dto.getNombre());
        req.setApellidoPaterno(dto.getApellidoPaterno());
        req.setApellidoMaterno(dto.getApellidoMaterno());
        req.setCorreoElectronico(dto.getCorreoElectronico());
        req.setIdAvatar(idAvatarSolicitado);

        UsuarioBasicoDTO actualizado = usuarioClient.actualizarPerfil(usuarioId, req);
        if (actualizado == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo actualizar: servicio de usuarios no disponible");
        }
        return construirProfileDto(actualizado);
    }

    public ProfileDto cambiarAvatar(Integer usuarioId, Integer avatarId) {
        if (!avatarRepository.existsById(avatarId)) {
            throw new NoSuchElementException("El avatar solicitado no existe: " + avatarId);
        }
        ActualizarPerfilRequest req = new ActualizarPerfilRequest();
        req.setIdAvatar(avatarId);

        UsuarioBasicoDTO actualizado = usuarioClient.actualizarPerfil(usuarioId, req);
        if (actualizado == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo cambiar el avatar: servicio de usuarios no disponible");
        }
        return construirProfileDto(actualizado);
    }

    // --- MÉTODOS DE CRUD PARA EL CATÁLOGO DE AVATARES ---

    public List<AvatarDto> listarAvatares() {
        return avatarRepository.findAll().stream()
                .map(this::toAvatarDto)
                .collect(Collectors.toList());
    }

    public AvatarDto obtenerAvatar(Integer id) {
        AvatarEntity entity = avatarRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avatar no encontrado: " + id));
        return toAvatarDto(entity);
    }

    @Transactional
    public AvatarDto crearAvatar(AvatarDto dto) {
        AvatarEntity entity = new AvatarEntity(dto.getImagen(), dto.getNombre());
        return toAvatarDto(avatarRepository.save(entity));
    }

    @Transactional
    public AvatarDto actualizarAvatar(Integer id, AvatarDto dto) {
        AvatarEntity entity = avatarRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Avatar no encontrado: " + id));
        entity.setImagen(dto.getImagen());
        entity.setNombre(dto.getNombre());
        return toAvatarDto(avatarRepository.save(entity));
    }

    @Transactional
    public void eliminarAvatar(Integer id) {
        if (!avatarRepository.existsById(id)) {
            throw new NoSuchElementException("Avatar no encontrado: " + id);
        }
        avatarRepository.deleteById(id);
    }

    private AvatarDto toAvatarDto(AvatarEntity entity) {
        return new AvatarDto(entity.getId(), entity.getImagen(), entity.getNombre());
    }

    private ProfileDto construirProfileDto(UsuarioBasicoDTO u) {
        ProfileDto dto = new ProfileDto();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellidoPaterno(u.getApellidoPaterno());
        dto.setApellidoMaterno(u.getApellidoMaterno());
        dto.setCorreoElectronico(u.getCorreoElectronico());
        dto.setRol(u.getRol());

        if (u.getIdAvatar() != null) {
            AvatarEntity avatarEntity = avatarRepository.findById(u.getIdAvatar()).orElse(null);
            if (avatarEntity != null) {
                AvatarDto avatarDto = new AvatarDto();
                avatarDto.setId(avatarEntity.getId());
                avatarDto.setImagen(avatarEntity.getImagen());
                avatarDto.setNombre(avatarEntity.getNombre());
                dto.setAvatar(avatarDto);
            }
        }
        return dto;
    }
}