package com.example.m2_user_service.controller;

import com.example.m2_user_service.entity.Grupo;
import com.example.m2_user_service.repository.GrupoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
public class GrupoController {
    private final GrupoRepository grupoRepository;

    @GetMapping
    public ResponseEntity<List<Grupo>> listarGrupos() {
        return ResponseEntity.ok(grupoRepository.findAll());
    }
}