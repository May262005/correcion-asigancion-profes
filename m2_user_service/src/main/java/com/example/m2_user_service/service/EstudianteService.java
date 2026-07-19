package com.example.m2_user_service.service;

import com.example.m2_user_service.dto.EstudianteDTOs.*;
import com.example.m2_user_service.entity.*;
import com.example.m2_user_service.repository.*;
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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public EstudianteResponseDTO create(CreateEstudianteDTO dto) {
        if (usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
            throw new RuntimeException("Correo ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellidoPaterno(dto.getApellidoPaterno() != null ? dto.getApellidoPaterno() : "");
        usuario.setApellidoMaterno(dto.getApellidoMaterno() != null ? dto.getApellidoMaterno() : "");
        usuario.setCorreoElectronico(dto.getCorreoElectronico());
        
        // ✅ SI VIENE CONTRASEÑA DEL FRONTEND, USA ESA, SI NO USA LA DEFAULT
        String password = (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) 
            ? dto.getContrasena() 
            : "UTEQ123";
        usuario.setContrasena(passwordEncoder.encode(password));
        
        usuario.setRol(RolUsuario.estudiante);
        Usuario savedUser = usuarioRepository.save(usuario);

        Estudiante estudiante = new Estudiante();
        estudiante.setIdUsuario(savedUser.getId());
        estudiante.setIdGrupo(dto.getIdGrupo());
        estudiante.setMatricula(dto.getMatricula());
        Estudiante saved = estudianteRepository.save(estudiante);

        return toResponseDTO(saved);
    }

    public List<EstudianteResponseDTO> findAll() {
        return estudianteRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public EstudianteResponseDTO findOne(Integer id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        return toResponseDTO(estudiante);
    }

    @Transactional
    public EstudianteResponseDTO update(Integer id, UpdateEstudianteDTO dto) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Usuario usuario = usuarioRepository.findById(estudiante.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
        if (dto.getApellidoPaterno() != null) usuario.setApellidoPaterno(dto.getApellidoPaterno());
        if (dto.getApellidoMaterno() != null) usuario.setApellidoMaterno(dto.getApellidoMaterno());
        
        if (dto.getCorreoElectronico() != null && !dto.getCorreoElectronico().equals(usuario.getCorreoElectronico())) {
            if (usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
                throw new RuntimeException("Correo ya en uso");
            }
            usuario.setCorreoElectronico(dto.getCorreoElectronico());
        }
        usuarioRepository.save(usuario);

        if (dto.getIdGrupo() != null) estudiante.setIdGrupo(dto.getIdGrupo());
        if (dto.getMatricula() != null) estudiante.setMatricula(dto.getMatricula());

        Estudiante updated = estudianteRepository.save(estudiante);
        return toResponseDTO(updated);
    }

    @Transactional
    public void remove(Integer id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        Integer idUsuario = estudiante.getIdUsuario();
        estudianteRepository.deleteById(id);
        usuarioRepository.deleteById(idUsuario);
    }

    private EstudianteResponseDTO toResponseDTO(Estudiante estudiante) {
        Usuario usuario = usuarioRepository.findById(estudiante.getIdUsuario()).orElse(null);
        EstudianteResponseDTO dto = new EstudianteResponseDTO();
        dto.setId(estudiante.getId());
        if (usuario != null) {
            dto.setNombreCompleto(usuario.getNombre() + " " + usuario.getApellidoPaterno() + " " + usuario.getApellidoMaterno());
            dto.setCorreoElectronico(usuario.getCorreoElectronico());
        }
        dto.setIdGrupo(estudiante.getIdGrupo());
        dto.setMatricula(estudiante.getMatricula());
        return dto;
    }
}