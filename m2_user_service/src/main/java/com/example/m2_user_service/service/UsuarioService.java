package com.example.m2_user_service.service;

import com.example.m2_user_service.client.GrupoClient;
import com.example.m2_user_service.dto.EstudianteDTOs.EstudianteResponseDTO;
import com.example.m2_user_service.dto.GrupoDTO;
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
    private final GrupoClient grupoClient;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ======================================================
    // OBTENER PERFIL COMPLETO
    // ======================================================
    public PerfilCompletoDTO obtenerPerfil(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PerfilCompletoDTO response = new PerfilCompletoDTO();

        // Datos generales usuario
        UsuarioResponseDTO userDTO = new UsuarioResponseDTO();
        userDTO.setId(usuario.getId());
        userDTO.setNombre(usuario.getNombre());
        userDTO.setApellidoPaterno(usuario.getApellidoPaterno());
        userDTO.setApellidoMaterno(usuario.getApellidoMaterno());
        userDTO.setCorreoElectronico(usuario.getCorreoElectronico());
        userDTO.setRol(usuario.getRol().name());
        response.setUsuario(userDTO);

        // ======================================================
        // DATOS EXTRA ESTUDIANTE
        // ======================================================
        if (usuario.getRol() == RolUsuario.estudiante) {
            Estudiante estudiante = estudianteRepository.findByIdUsuario(usuario.getId()).orElse(null);

            if (estudiante != null) {
                EstudianteResponseDTO extra = new EstudianteResponseDTO();
                extra.setId(estudiante.getId());
                extra.setMatricula(estudiante.getMatricula());
                extra.setNombreCompleto(usuario.getNombre() + " " + usuario.getApellidoPaterno() + " " + usuario.getApellidoMaterno());
                extra.setCorreoElectronico(usuario.getCorreoElectronico());

                // Obtener grupo desde M8
                try {
                    GrupoDTO grupo = grupoClient.obtenerGrupo(estudiante.getIdGrupo());
                    extra.setGrupo(grupo);
                } catch (Exception e) {
                    extra.setGrupo(null);
                }

                response.setExtra(extra);
            }
        }

        // ======================================================
        // DATOS EXTRA PROFESOR
        // ======================================================
        else if (usuario.getRol() == RolUsuario.profesor) {
            Profesor profesor = profesorRepository.findByIdUsuario(usuario.getId()).orElse(null);
            response.setExtra(profesor);
        }

        return response;
    }

    // ======================================================
    // OBTENER USUARIO CON SU ROL
    // ======================================================
    public UsuarioRolDTO getUsuarioRol(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioRolDTO dto = new UsuarioRolDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreoElectronico(usuario.getCorreoElectronico());
        dto.setRol(usuario.getRol().name());

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

    // ======================================================
    // ACTUALIZAR PERFIL
    // ======================================================
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

        if (dto.getNombre() != null) {
            usuario.setNombre(dto.getNombre());
        }

        if (dto.getApellidoPaterno() != null) {
            usuario.setApellidoPaterno(dto.getApellidoPaterno());
        }

        if (dto.getApellidoMaterno() != null) {
            usuario.setApellidoMaterno(dto.getApellidoMaterno());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(dto.getPassword()));
        }

        Usuario actualizado = usuarioRepository.save(usuario);

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(actualizado.getId());
        response.setNombre(actualizado.getNombre());
        response.setApellidoPaterno(actualizado.getApellidoPaterno());
        response.setApellidoMaterno(actualizado.getApellidoMaterno());
        response.setCorreoElectronico(actualizado.getCorreoElectronico());
        response.setRol(actualizado.getRol().name());

        return response;
    }
}