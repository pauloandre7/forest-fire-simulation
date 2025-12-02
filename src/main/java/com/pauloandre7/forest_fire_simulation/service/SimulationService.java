package com.pauloandre7.forest_fire_simulation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.pauloandre7.forest_fire_simulation.model.Cell;
import com.pauloandre7.forest_fire_simulation.model.CellState;
import com.pauloandre7.forest_fire_simulation.model.Forest;
import com.pauloandre7.forest_fire_simulation.model.WindDirection;

@Service
public class SimulationService {
    
    private final double BASE_BURNING_PROBABILITY = 0.4;
    private Forest currentForest;
    
    public SimulationService(Forest currentForest){
        this.currentForest = currentForest;
    }

    public void generateRandomForest(int height, int width, int burningTime){

        Random random = new Random();

        List<List<Cell>> forestCells = new ArrayList<>();
        List<Cell> lineCells = new ArrayList<>();

        // get the values of Enum CellState and parse to List.
        List<CellState> cellStates = new ArrayList<>(Arrays.asList(CellState.values()));

        // remove Burning from the list, because i don't want to randomize this value right now
        cellStates.remove(CellState.BURNING);

        for(int i = 0; i < height; i++){

            lineCells.clear();
            for(int j = 0; j < height; j++){
                
                int randomIndex = random.nextInt(cellStates.size());
                double randomMoisture = random.nextDouble(1.0);
                double randomRelief = random.nextDouble(1.0);

                lineCells.add(new Cell(i, j, cellStates.get(randomIndex), randomMoisture, randomRelief));
            }
            forestCells.add(lineCells);
        }

        // now the fire starting point will be setted using random index.
        forestCells.get(random.nextInt(height)).get(random.nextInt(width)).startBurning(burningTime);

        List<WindDirection> windDirections = new ArrayList<>(Arrays.asList(WindDirection.values()));
        WindDirection randomWindDirection = windDirections.get(random.nextInt(windDirections.size()));

        this.currentForest = new Forest(height, width, forestCells, randomWindDirection, random.nextDouble(1.0), BASE_BURNING_PROBABILITY);
    }

    public void initializeForest(int height, int width, List<List<Cell>> forestCells, 
        WindDirection windDirection, double windSpeed){
        
        this.currentForest = new Forest(height, width, forestCells, windDirection, 
            windSpeed, BASE_BURNING_PROBABILITY);
    }
}
