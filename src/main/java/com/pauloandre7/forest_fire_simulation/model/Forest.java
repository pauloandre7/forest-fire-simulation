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
    
    private final int height;
    private final int width;

    @Getter(AccessLevel.NONE) // will not creat the getter for this one.
    private final List<List<Cell>> cells;
    private final WindDirection windDirection;
    private final double windSpeed;
    private final int burningTime;
    private final double baseBurningProbability;

    @JsonCreator
    public Forest(@JsonProperty("rows") int height,
                  @JsonProperty("cols") int width,
                  @JsonProperty("cells") List<List<Cell>> cells,
                  @JsonProperty("windDirection") WindDirection windDirection,
                  @JsonProperty("windSpeed") double windSpeed,
                  @JsonProperty("burningTime") int burningTime,
                  @JsonProperty("baseBurningProbability") double baseBurningProbability

    ){
        this.height = height;
        this.width = width;
        this.cells = cells;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.burningTime = burningTime;
        this.baseBurningProbability = baseBurningProbability;
    }

    public Forest(Forest originalForest){
        this.height = originalForest.height;
        this.width = originalForest.width;
        this.cells = originalForest.cells;
        this.windDirection = originalForest.windDirection;
        this.windSpeed = originalForest.windSpeed;
        this.burningTime = originalForest.burningTime;
        this.baseBurningProbability = originalForest.baseBurningProbability;
    }

    public List<List<Cell>> getCells(){
        // This method return the list, but she cannot be modified
        // This class cannot let the matriz be modified by a set() method.
        return Collections.unmodifiableList(cells);
    }
}
