package com.example.m3_prifile_service.repository;
import com.example.m3_prifile_service.entity.UsuarioProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la tabla public.usuario,
 * enfocado en las operaciones de perfil e interfaz.
 */
@Repository
public interface UsuarioProfileRepository extends JpaRepository<UsuarioProfileEntity, Integer> {

    Optional<UsuarioProfileEntity> findByCorreoElectronico(String correo);

    /**
     * Actualiza únicamente el avatar del usuario.
     * Solo toca la columna id_avatar — no afecta contraseña ni rol.
     */
    @Modifying
    @Query("UPDATE UsuarioProfileEntity u SET u.avatar.id = :avatarId WHERE u.id = :usuarioId")
    int actualizarAvatar(@Param("usuarioId") Integer usuarioId,
                         @Param("avatarId")  Integer avatarId);
}