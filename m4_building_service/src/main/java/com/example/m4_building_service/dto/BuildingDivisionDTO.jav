package com.example.m4_building_service.dto;

import lombok.Data;

@Data
public class BuildingDivisionDTO {
    private Long id;
    private Long idDivision;

    public BuildingDivisionDTO() {}

    public BuildingDivisionDTO(Long id, Long idDivision) {
        this.id = id;
        this.idDivision = idDivision;
    }
}