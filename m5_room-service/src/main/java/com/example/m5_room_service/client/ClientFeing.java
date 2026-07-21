package com.example.m5_room_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.m5_room_service.dto.BuildingDTO;

@FeignClient(name = "m4-building-service")
public interface ClientFeing {

    @GetMapping("/edificios/{id}")
    BuildingDTO getBuildingById(@PathVariable("id") Long id);
}