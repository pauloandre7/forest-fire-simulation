package com.pauloandre7.forest_fire_simulation.model;

import lombok.Getter;

/**
 * @author pauloandre7
 * 
 * This model is designed to represent a cell in relation to the main 
 * cell being calculated by the thread.
 */
@Getter
public class BurningCell extends Cell{
    // The cell direction can increase the wind effect.
    private final Direction cellDirection;

    public BurningCell(Cell cell, Direction cellDirection){
        
        super(cell.getRow(), cell.getCol(), cell.getState(), 
            cell.getMoisture(), cell.getRelief()
        );

        this.cellDirection = cellDirection;
    }

}
