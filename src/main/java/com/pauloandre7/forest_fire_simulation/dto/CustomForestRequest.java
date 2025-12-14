package com.pauloandre7.forest_fire_simulation.dto;

import java.util.List;

import com.pauloandre7.forest_fire_simulation.model.Cell;
import com.pauloandre7.forest_fire_simulation.model.Direction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author pauloandre7
 * 
 * Class to optimize the communication between controller and service. SpringBoot
 * will use the lombok constructor with the Post request body.
 */
@Getter
@RequiredArgsConstructor
public class CustomForestRequest {
    private final int height;
    private final int width;
    private final List<List<Cell>> forestCells;
    private final Direction windDirection;
    private final double windSpeed;
    private final int burningTime;
}
