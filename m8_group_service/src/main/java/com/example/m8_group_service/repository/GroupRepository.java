    package com.example.m8_group_service.repository;

    import com.example.m8_group_service.entity.GroupEntity;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;

    import java.util.List;
    import java.util.Optional;

    /**
     * Repositorio para la tabla public.grupo.
     */
    @Repository
    public interface GroupRepository extends JpaRepository<GroupEntity, Integer> {

        /** Buscar por nombre exacto (único en BD) */
        Optional<GroupEntity> findByNombre(String nombre);

        /** Verificar si ya existe un nombre (útil para validaciones en servicio) */
        boolean existsByNombre(String nombre);

        /** Todos los grupos de un turno específico */
        List<GroupEntity> findByIdTurno(Integer idTurno);

        /** Todos los grupos de una división específica */
        List<GroupEntity> findByIdDivision(Integer idDivision);

        /** Todos los grupos asignados a un tutor */
        List<GroupEntity> findByTutorId(Integer tutorId);

        /**
         * Busqueda por grado dentro de una división.
         * Útil para filtros de la vista de horarios.
         */
        @Query("SELECT g FROM GroupEntity g WHERE g.idDivision = :divId AND g.grado = :grado")
        List<GroupEntity> findByDivisionAndGrado(@Param("divId")  Integer divisionId,
                                                @Param("grado") String grado);
    }