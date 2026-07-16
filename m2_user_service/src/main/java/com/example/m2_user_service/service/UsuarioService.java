package com.example.m2_user_service.service;

import com.example.m2_user_service.dto.UsuarioDTOs.*;
import com.example.m2_user_service.dto.UsuarioRolDTO;
import com.example.m2_user_service.entity.*;
import com.example.m2_user_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ProfesorRepository profesorRepository;
    private final EstudianteRepository estudianteRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PerfilCompletoDTO obtenerPerfil(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PerfilCompletoDTO response = new PerfilCompletoDTO();
        
        UsuarioResponseDTO userDTO = new UsuarioResponseDTO();
        userDTO.setId(usuario.getId());
        userDTO.setNombre(usuario.getNombre());
        userDTO.setApellidoPaterno(usuario.getApellidoPaterno());
        userDTO.setApellidoMaterno(usuario.getApellidoMaterno());
        userDTO.setCorreoElectronico(usuario.getCorreoElectronico());
        userDTO.setRol(usuario.getRol().name());
        response.setUsuario(userDTO);

        Object extra = null;
        if (usuario.getRol() == RolUsuario.profesor) {
            extra = profesorRepository.findByIdUsuario(usuario.getId()).orElse(null);
        } else if (usuario.getRol() == RolUsuario.estudiante) {
            extra = estudianteRepository.findByIdUsuario(usuario.getId()).orElse(null);
        }
        response.setExtra(extra);

        return response;
    }

    public UsuarioRolDTO getUsuarioRol(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioRolDTO dto = new UsuarioRolDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreoElectronico(usuario.getCorreoElectronico());
        dto.setRol(usuario.getRol().name());

        // ✅ Buscar el idRol (profesor o estudiante)
        Integer idRol = null;
        if (usuario.getRol() == RolUsuario.profesor) {
            Profesor profesor = profesorRepository.findByIdUsuario(usuario.getId()).orElse(null);
            if (profesor != null) {
                idRol = profesor.getId();
            }
        } else if (usuario.getRol() == RolUsuario.estudiante) {
            Estudiante estudiante = estudianteRepository.findByIdUsuario(usuario.getId()).orElse(null);
            if (estudiante != null) {
                idRol = estudiante.getId();
            }
        }
        dto.setIdRol(idRol);

        return dto;
    }

    @Transactional
    public UsuarioResponseDTO actualizarPerfil(Integer id, ActualizarPerfilDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getCorreoElectronico() != null && !dto.getCorreoElectronico().equals(usuario.getCorreoElectronico())) {
            if (usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
                throw new RuntimeException("El correo electrónico ya está en uso");
            }
            usuario.setCorreoElectronico(dto.getCorreoElectronico());
        }

        if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
        if (dto.getApellidoPaterno() != null) usuario.setApellidoPaterno(dto.getApellidoPaterno());
        if (dto.getApellidoMaterno() != null) usuario.setApellidoMaterno(dto.getApellidoMaterno());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(dto.getPassword()));
        }

        Usuario updated = usuarioRepository.save(usuario);

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(updated.getId());
        response.setNombre(updated.getNombre());
        response.setApellidoPaterno(updated.getApellidoPaterno());
        response.setApellidoMaterno(updated.getApellidoMaterno());
        response.setCorreoElectronico(updated.getCorreoElectronico());
        response.setRol(updated.getRol().name());
        return response;
    }
}