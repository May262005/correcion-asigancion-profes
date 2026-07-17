package com.example.m3_prifile_service.repository;
import com.example.m3_prifile_service.entity.AvatarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la tabla public.avatar.
 * Extiende JpaRepository para obtener CRUD completo sin código extra.
 */
@Repository
public interface AvatarRepository extends JpaRepository<AvatarEntity, Integer> {

    /** Buscar avatar por nombre exacto */
    Optional<AvatarEntity> findByNombre(String nombre);
}