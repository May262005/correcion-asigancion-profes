package com.example.m2_user_service.service;

import com.example.m2_user_service.client.GrupoClient;
import com.example.m2_user_service.dto.EstudianteDTOs.*;
import com.example.m2_user_service.dto.GrupoDTO;
import com.example.m2_user_service.entity.Estudiante;
import com.example.m2_user_service.entity.RolUsuario;
import com.example.m2_user_service.entity.Usuario;
import com.example.m2_user_service.repository.EstudianteRepository;
import com.example.m2_user_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    // Cliente Feign hacia m8-group-service
    private final GrupoClient grupoClient;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ======================================================
    // CREAR ESTUDIANTE
    // ======================================================
    @Transactional
    public EstudianteResponseDTO create(CreateEstudianteDTO dto) {
        if (usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
            throw new RuntimeException("Correo ya registrado");
        }

        // ===========================================
        // VALIDAR GRUPO CON MICROSERVICIO M8
        // ===========================================
        GrupoDTO grupo;
        try {
            grupo = grupoClient.obtenerGrupo(dto.getIdGrupo());
        } catch (Exception e) {
            throw new RuntimeException("El grupo no existe o m8-group-service no está disponible");
        }

        // ===========================================
        // CREAR USUARIO
        // ===========================================
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellidoPaterno(dto.getApellidoPaterno() != null ? dto.getApellidoPaterno() : "");
        usuario.setApellidoMaterno(dto.getApellidoMaterno() != null ? dto.getApellidoMaterno() : "");
        usuario.setCorreoElectronico(dto.getCorreoElectronico());

        String password = (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) 
            ? dto.getContrasena() 
            : "UTEQ123";
        usuario.setContrasena(passwordEncoder.encode(password));
        usuario.setRol(RolUsuario.estudiante);

        Usuario savedUser = usuarioRepository.save(usuario);

        // ===========================================
        // CREAR ESTUDIANTE
        // ===========================================
        Estudiante estudiante = new Estudiante();
        estudiante.setIdUsuario(savedUser.getId());
        // Guardamos solamente el ID
        estudiante.setIdGrupo(grupo.getId());
        estudiante.setMatricula(dto.getMatricula());

        Estudiante saved = estudianteRepository.save(estudiante);

        return toResponseDTO(saved);
    }

    // ======================================================
    // LISTAR TODOS
    // ======================================================
    public List<EstudianteResponseDTO> findAll() {
        return estudianteRepository
            .findAll()
            .stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    // ======================================================
    // BUSCAR UNO
    // ======================================================
    public EstudianteResponseDTO findOne(Integer id) {
        Estudiante estudiante = estudianteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return toResponseDTO(estudiante);
    }

    // ======================================================
    // ACTUALIZAR
    // ======================================================
    @Transactional
    public EstudianteResponseDTO update(Integer id, UpdateEstudianteDTO dto) {
        Estudiante estudiante = estudianteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Usuario usuario = usuarioRepository.findById(estudiante.getIdUsuario())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getNombre() != null)
            usuario.setNombre(dto.getNombre());

        if (dto.getApellidoPaterno() != null)
            usuario.setApellidoPaterno(dto.getApellidoPaterno());

        if (dto.getApellidoMaterno() != null)
            usuario.setApellidoMaterno(dto.getApellidoMaterno());

        if (dto.getCorreoElectronico() != null && 
            !dto.getCorreoElectronico().equals(usuario.getCorreoElectronico())) {
            if (usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
                throw new RuntimeException("Correo ya en uso");
            }
            usuario.setCorreoElectronico(dto.getCorreoElectronico());
        }

        usuarioRepository.save(usuario);

        // Cambiar grupo
        if (dto.getIdGrupo() != null) {
            try {
                GrupoDTO grupo = grupoClient.obtenerGrupo(dto.getIdGrupo());
                estudiante.setIdGrupo(grupo.getId());
            } catch (Exception e) {
                throw new RuntimeException("Grupo no válido");
            }
        }

        if (dto.getMatricula() != null)
            estudiante.setMatricula(dto.getMatricula());

        Estudiante actualizado = estudianteRepository.save(estudiante);

        return toResponseDTO(actualizado);
    }

    // ======================================================
    // ELIMINAR
    // ======================================================
    @Transactional
    public void remove(Integer id) {
        Estudiante estudiante = estudianteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Integer idUsuario = estudiante.getIdUsuario();

        estudianteRepository.deleteById(id);
        usuarioRepository.deleteById(idUsuario);
    }

    // ======================================================
    // CONVERTIR RESPONSE
    // ======================================================
    private EstudianteResponseDTO toResponseDTO(Estudiante estudiante) {
        EstudianteResponseDTO dto = new EstudianteResponseDTO();
        dto.setId(estudiante.getId());
        dto.setMatricula(estudiante.getMatricula());

        Usuario usuario = usuarioRepository.findById(estudiante.getIdUsuario()).orElse(null);
        if (usuario != null) {
            dto.setNombreCompleto(usuario.getNombre() + " " + 
                usuario.getApellidoPaterno() + " " + 
                usuario.getApellidoMaterno());
            dto.setCorreoElectronico(usuario.getCorreoElectronico());
        }

        // Traer grupo desde m8
        try {
            GrupoDTO grupo = grupoClient.obtenerGrupo(estudiante.getIdGrupo());
            dto.setGrupo(grupo);
        } catch (Exception e) {
            dto.setGrupo(null);
        }

        return dto;
    }
}