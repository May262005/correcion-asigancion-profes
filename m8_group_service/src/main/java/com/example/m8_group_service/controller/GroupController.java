    package com.example.m8_group_service.controller;

    import com.example.m8_group_service.dto.GroupDto;
    import com.example.m8_group_service.service.GroupService;
    import jakarta.validation.Valid;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.NoSuchElementException;

    /**
     * Controlador REST del microservicio m8_group_service.
     *
     * Base URL: /api/v1/grupos
     *
     * ──────────────────────────────────────────────────────────────
     * CRUD PRINCIPAL
     * ──────────────────────────────────────────────────────────────
     *  GET    /api/v1/grupos                      → listar todos
     *  GET    /api/v1/grupos/{id}                 → obtener uno
     *  POST   /api/v1/grupos                      → crear
     *  PUT    /api/v1/grupos/{id}                 → actualizar completo
     *  DELETE /api/v1/grupos/{id}                 → eliminar
     *
     * ──────────────────────────────────────────────────────────────
     * FILTROS
     * ──────────────────────────────────────────────────────────────
     *  GET    /api/v1/grupos/turno/{idTurno}      → por turno
     *  GET    /api/v1/grupos/division/{idDiv}     → por división
     *  GET    /api/v1/grupos/tutor/{tutorId}      → por tutor
     *  GET    /api/v1/grupos/division/{idDiv}/grado/{grado}
     */
    @RestController
    @RequestMapping("/api/v1/grupos")
    public class GroupController {

        private final GroupService groupService;

        public GroupController(GroupService groupService) {
            this.groupService = groupService;
        }

        // ══════════════════════════════════════════════
        // CRUD
        // ══════════════════════════════════════════════

        /** GET /api/v1/grupos */
        @GetMapping
        public ResponseEntity<List<GroupDto>> listarTodos() {
            return ResponseEntity.ok(groupService.listarTodos());
        }

        /** GET /api/v1/grupos/{id} */
        @GetMapping("/{id}")
        public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
            try {
                return ResponseEntity.ok(groupService.obtenerPorId(id));
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }

        /**
         * POST /api/v1/grupos
         *
         * Body JSON de ejemplo:
         * {
         *   "idTurno": 1,
         *   "idDivision": 2,
         *   "tutorId": 5,
         *   "nombre": "3°A Sistemas",
         *   "abreviatura": "3A-SIS",
         *   "grado": "3",
         *   "colorIdentificador": "#3A7BD5"
         * }
         */
        @PostMapping
        public ResponseEntity<?> crear(@Valid @RequestBody GroupDto dto) {
            try {
                GroupDto creado = groupService.crear(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(creado);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
        }

        /**
         * PUT /api/v1/grupos/{id}
         * Actualiza todos los campos del grupo.
         */
        @PutMapping("/{id}")
        public ResponseEntity<?> actualizar(@PathVariable Integer id,
                                            @Valid @RequestBody GroupDto dto) {
            try {
                return ResponseEntity.ok(groupService.actualizar(id, dto));
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
        }

        /**
         * DELETE /api/v1/grupos/{id}
         * Retorna 204 No Content si se eliminó correctamente.
         * La BD lanzará error si hay estudiantes con RESTRICT.
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<?> eliminar(@PathVariable Integer id) {
            try {
                groupService.eliminar(id);
                return ResponseEntity.noContent().build();
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (Exception e) {
                // Captura violación de FK (estudiantes asignados)
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                    .body("No se puede eliminar: el grupo tiene estudiantes asignados.");
            }
        }

        // ══════════════════════════════════════════════
        // FILTROS
        // ══════════════════════════════════════════════

        /** GET /api/v1/grupos/turno/{idTurno} */
        @GetMapping("/turno/{idTurno}")
        public ResponseEntity<List<GroupDto>> porTurno(@PathVariable Integer idTurno) {
            return ResponseEntity.ok(groupService.porTurno(idTurno));
        }

        /** GET /api/v1/grupos/division/{idDivision} */
        @GetMapping("/division/{idDivision}")
        public ResponseEntity<List<GroupDto>> porDivision(@PathVariable Integer idDivision) {
            return ResponseEntity.ok(groupService.porDivision(idDivision));
        }

        /** GET /api/v1/grupos/tutor/{tutorId} */
        @GetMapping("/tutor/{tutorId}")
        public ResponseEntity<List<GroupDto>> porTutor(@PathVariable Integer tutorId) {
            return ResponseEntity.ok(groupService.porTutor(tutorId));
        }

        /** GET /api/v1/grupos/division/{idDivision}/grado/{grado} */
        @GetMapping("/division/{idDivision}/grado/{grado}")
        public ResponseEntity<List<GroupDto>> porDivisionYGrado(
                @PathVariable Integer idDivision,
                @PathVariable String  grado) {
            return ResponseEntity.ok(groupService.porDivisionYGrado(idDivision, grado));
        }
    }