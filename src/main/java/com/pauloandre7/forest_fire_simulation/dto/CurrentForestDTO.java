package com.pauloandre7.forest_fire_simulation.dto;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author pauloandre7
 * 
 * This class optimize the communication between Service and Controller and protect Forest model.
 */
@Getter
@RequiredArgsConstructor
public class CurrentForestDTO {

    private final List<List<CellStateDTO>> grid;
    private final int currentGeneration;
    private final boolean isRunning;
}
