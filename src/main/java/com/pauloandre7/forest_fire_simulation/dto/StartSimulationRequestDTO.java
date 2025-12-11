package com.pauloandre7.forest_fire_simulation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StartSimulationRequestDTO {
    private final int maxGeneration;
}
