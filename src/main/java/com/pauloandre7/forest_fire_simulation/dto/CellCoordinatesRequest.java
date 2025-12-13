package com.pauloandre7.forest_fire_simulation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author pauloandre7
 * 
 * DTO class to receive the body of igniteCell request. Will be used to
 * get the coordinates to ignite a specifc cell.
 */
@Getter
@RequiredArgsConstructor
public class CellCoordinatesRequest {
    
    private final int x;
    private final int y;
}
