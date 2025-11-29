package com.pauloandre7.forest_fire_simulation.model;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author pauloandre7
 * 
 * This class represents the grid wich contains all the cells.
 */
@Getter
@ToString
@EqualsAndHashCode
public class Forest {
    
    private final int rows;
    private final int cols;

    @Getter(AccessLevel.NONE) // will not creat the getter for this one.
    private final List<List<Cell>> cells;
    private final WindDirection windDirection;
    private final double windSpeed;
    private final double baseBurningProbability;

    @JsonCreator
    public Forest(@JsonProperty("rows") int rows,
                  @JsonProperty("cols") int cols,
                  @JsonProperty("cells") List<List<Cell>> cells,
                  @JsonProperty("windDirection") WindDirection windDirection,
                  @JsonProperty("windSpeed") double windSpeed,
                  @JsonProperty("baseBurningProbability") double baseBurningProbability

    ){
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.baseBurningProbability = baseBurningProbability;
    }

    public List<List<Cell>> getCells(){
        // This method return the list, but she cannot be modified
        // This class cannot let the matriz be modified by a set() method.
        return Collections.unmodifiableList(cells);
    }
}
