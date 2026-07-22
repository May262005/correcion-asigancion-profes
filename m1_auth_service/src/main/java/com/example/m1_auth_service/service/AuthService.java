package com.example.m1_auth_service.service;

import com.example.m1_auth_service.client.UserServiceClient;
import com.example.m1_auth_service.dto.AuthDTOs.*;
import com.example.m1_auth_service.dto.UsuarioRolDTO;
import com.example.m1_auth_service.entity.Usuario;
import com.example.m1_auth_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserServiceClient userServiceClient;  // 👈 INYECTAR FEIGN CLIENT

    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(dto.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(usuario.getId(), usuario.getRol().name());

        
        // ✅ OBTENER idRol desde User-Service
        Integer idRol = null;
        try {
            UsuarioRolDTO usuarioRol = userServiceClient.getUsuarioRol(usuario.getId());
            idRol = usuarioRol.getIdRol();
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo obtener idRol para usuario: " + usuario.getId());
            e.printStackTrace(); // 👈 vuelve a agregar esto
        }
        AuthResponseDTO response = new AuthResponseDTO();
        response.setId(usuario.getId());
        response.setIdRol(idRol);  // ✅ AHORA TIENE VALOR
        response.setNombre(usuario.getNombre());
        response.setCorreoElectronico(usuario.getCorreoElectronico());
        response.setRol(usuario.getRol().name());
        response.setToken(token);
        
        return response;
    }

    public Usuario validarToken(String token) {
        Integer userId = jwtService.getUserIdFromToken(token);
        return usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Token inválido"));
    }
}