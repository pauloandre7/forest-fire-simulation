package com.pauloandre7.forest_fire_simulation.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author pauloandre7
 * 
 * This class represents each cell of the cellular automaton grid. As a cell, she has all the
 * attributes to store the position, state, burning time and potentianting factors (moisture and relief)
 */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Cell {
    // position in matrix
    private final int row;
    private final int col;
    
    private CellState state;
    
    // To count the burning time before ash.
    private int burningTime;

    // Levels of moisture and relief in %.
    private final double moisture;
    private final double relief;

    // This constructor lets me keep row and col final without causing error in the JsonCreator
    @JsonCreator
    public Cell(@JsonProperty("row") int row,
                @JsonProperty("col") int col,
                @JsonProperty("state") CellState state,
                @JsonProperty("moisture") double moisture,
                @JsonProperty("relief") double relief
    ){
        this.row = row;
        this.col = col;
        this.state = state;
        this.moisture = moisture;
        this.relief = relief;
    }

    /* Copy Constructor to prevent the application logic from errors at paralelization.
        Other threads can take this object to get the data, so its important to maintain 
        those data safe.
    */
    public Cell(Cell cellOriginal){
        this.row = cellOriginal.row;
        this.col = cellOriginal.col;
        this.state = cellOriginal.state;
        this.burningTime = cellOriginal.burningTime;
        this.moisture = cellOriginal.moisture;
        this.relief = cellOriginal.relief;
    }

    public void startBurning(int initialBurningTime){
        if(this.state.equals(CellState.VEGETATION)){
            this.state = CellState.BURNING;
            this.burningTime = initialBurningTime;
        }
    }

    public void updateBurningState(){
        if (this.state.equals(CellState.BURNING)) {
            if (this.burningTime > 0) { 
                this.burningTime -= 1;
            } else { 
                this.state = CellState.ASH;
                this.burningTime = 0; 
            }
        }
    }
}
