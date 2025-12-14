package com.pauloandre7.forest_fire_simulation.dto;

import com.pauloandre7.forest_fire_simulation.model.CellState;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author pauloandre7
 * 
 * DTO to optimize the communication between back and front-end. The service will create
 * this simple DTO to update the simulation on front-end.
 */
@Getter
@RequiredArgsConstructor
public class CellStateDTO {
    
    private final CellState state;
}
