package com.example.m2_user_service.client;

import com.example.m2_user_service.dto.GrupoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "m8-group-service")
public interface GrupoClient {


    @GetMapping("/api/v1/grupos/{id}")
    GrupoDTO obtenerGrupo(
            @PathVariable("id") Integer id
    );

}