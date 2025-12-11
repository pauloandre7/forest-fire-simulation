package com.pauloandre7.forest_fire_simulation.dto;

import java.util.List;

import com.pauloandre7.forest_fire_simulation.model.Cell;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author pauloandre7
 * 
 * This class optimize the communication between Service and Controller and protect Forest model.
 */
@Getter
@AllArgsConstructor
public class SimulationStatusDTO {

    private List<List<Cell>> grid;
    private int currentGeneration;
    private boolean isRunning;
}
