package com.pauloandre7.forest_fire_simulation.dto;

import java.util.List;

import com.pauloandre7.forest_fire_simulation.model.Cell;

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

    private final List<List<Cell>> grid;
    private final int currentGeneration;
    private final boolean isRunning;
}
