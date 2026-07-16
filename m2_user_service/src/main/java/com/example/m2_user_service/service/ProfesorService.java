package com.example.m2_user_service.service;

import com.example.m2_user_service.dto.ProfesorDTOs.*;
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
public class ProfesorService {
    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<ProfesorResponseDTO> findAllPsicologos() {
        return profesorRepository.findByEsPsicologoTrue().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProfesorResponseDTO create(CreateProfesorDTO dto) {
        if (usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellidoPaterno(dto.getApellidoPaterno() != null ? dto.getApellidoPaterno() : "");
        usuario.setApellidoMaterno(dto.getApellidoMaterno() != null ? dto.getApellidoMaterno() : "");
        usuario.setCorreoElectronico(dto.getCorreoElectronico());
        usuario.setContrasena(passwordEncoder.encode("12345678"));
        usuario.setRol(RolUsuario.profesor);
        Usuario savedUser = usuarioRepository.save(usuario);

        String abreviatura = String.valueOf(dto.getNombre().charAt(0))
                + (dto.getApellidoPaterno() != null && !dto.getApellidoPaterno().isEmpty() ? dto.getApellidoPaterno().charAt(0) : "X")
                + (dto.getApellidoMaterno() != null && !dto.getApellidoMaterno().isEmpty() ? dto.getApellidoMaterno().charAt(0) : "X");
        abreviatura = abreviatura.toUpperCase();

        Profesor profesor = new Profesor();
        profesor.setIdUsuario(savedUser.getId());
        profesor.setAbreviaturaNombre(abreviatura);
        profesor.setTelefono(dto.getTelefono());
        profesor.setTitulo(dto.getTitulo());
        profesor.setEsPsicologo(dto.getEsPsicologo() != null ? dto.getEsPsicologo() : false);
        profesor.setColorCalendario("#3abef9");

        Profesor saved = profesorRepository.save(profesor);
        return toResponseDTO(saved);
    }

    public List<ProfesorResponseDTO> findAll() {
        return profesorRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProfesorResponseDTO findOne(Integer id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
        return toResponseDTO(profesor);
    }

    @Transactional
    public ProfesorResponseDTO update(Integer id, UpdateProfesorDTO dto) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        Usuario usuario = usuarioRepository.findById(profesor.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getNombre() != null || dto.getApellidoPaterno() != null || 
            dto.getApellidoMaterno() != null || dto.getCorreoElectronico() != null) {
            
            if (dto.getCorreoElectronico() != null && !dto.getCorreoElectronico().equals(usuario.getCorreoElectronico())) {
                if (usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
                    throw new RuntimeException("El correo electrónico ya está en uso");
                }
                usuario.setCorreoElectronico(dto.getCorreoElectronico());
            }
            if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
            if (dto.getApellidoPaterno() != null) usuario.setApellidoPaterno(dto.getApellidoPaterno());
            if (dto.getApellidoMaterno() != null) usuario.setApellidoMaterno(dto.getApellidoMaterno());
            usuarioRepository.save(usuario);
        }

        if (dto.getTelefono() != null) profesor.setTelefono(dto.getTelefono());
        if (dto.getTitulo() != null) profesor.setTitulo(dto.getTitulo());
        if (dto.getEsPsicologo() != null) profesor.setEsPsicologo(dto.getEsPsicologo());

        if (dto.getNombre() != null || dto.getApellidoPaterno() != null || dto.getApellidoMaterno() != null) {
            String nombre = dto.getNombre() != null ? dto.getNombre() : usuario.getNombre();
            String apPaterno = dto.getApellidoPaterno() != null ? dto.getApellidoPaterno() : usuario.getApellidoPaterno();
            String apMaterno = dto.getApellidoMaterno() != null ? dto.getApellidoMaterno() : usuario.getApellidoMaterno();
            
            String abrev = String.valueOf(nombre.charAt(0))
                    + (apPaterno != null && !apPaterno.isEmpty() ? apPaterno.charAt(0) : "X")
                    + (apMaterno != null && !apMaterno.isEmpty() ? apMaterno.charAt(0) : "X");
            profesor.setAbreviaturaNombre(abrev.toUpperCase());
        }

        Profesor updated = profesorRepository.save(profesor);
        return toResponseDTO(updated);
    }

    @Transactional
    public void remove(Integer id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
        Integer idUsuario = profesor.getIdUsuario();
        profesorRepository.deleteById(id);
        usuarioRepository.deleteById(idUsuario);
    }

    private ProfesorResponseDTO toResponseDTO(Profesor profesor) {
        Usuario usuario = usuarioRepository.findById(profesor.getIdUsuario()).orElse(null);
        ProfesorResponseDTO dto = new ProfesorResponseDTO();
        dto.setId(profesor.getId());
        if (usuario != null) {
            dto.setNombreCompleto(usuario.getNombre() + " " + usuario.getApellidoPaterno() + " " + usuario.getApellidoMaterno());
            dto.setCorreoElectronico(usuario.getCorreoElectronico());
        }
        dto.setTelefono(profesor.getTelefono());
        dto.setTitulo(profesor.getTitulo());
        dto.setEsPsicologo(profesor.getEsPsicologo());
        dto.setAbreviatura(profesor.getAbreviaturaNombre());
        return dto;
    }
}