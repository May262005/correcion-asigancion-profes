package com.example.m4_building_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.m4_building_service.dto.DivisionDTO;

@FeignClient(name = "m13-division-service")
public interface ClientFeing {

    @GetMapping("/divisions/{id}")
    DivisionDTO getDivisionById(@PathVariable("id") Long id);
}