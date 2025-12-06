package com.pauloandre7.forest_fire_simulation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.stereotype.Service;

import com.pauloandre7.forest_fire_simulation.model.Cell;
import com.pauloandre7.forest_fire_simulation.model.CellState;
import com.pauloandre7.forest_fire_simulation.model.Direction;
import com.pauloandre7.forest_fire_simulation.model.Forest;
import com.pauloandre7.forest_fire_simulation.parallel.SimulationTask;

@Service
public class SimulationService {
    // one cell has 8 neighbors. each neighbor burning will increase base Prob. in 0.12
    private final double BASE_BURNING_PROBABILITY = 0.125;
    private Forest currentForest;
    private final ExecutorService executor;
    private boolean isRunning = false;

    public SimulationService(){
        // get the amount of available threads and creates a pool for them
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(numberOfThreads);
    }

    public void startSimulation(){
        isRunning = true;
    }

    public void stopSimulation(){
        isRunning = false;
    }

    // with this method, the sheduler will know the current service status
    public boolean isRunning(){
        return isRunning;
    }

    public void generateRandomForest(int height, int width, int burningTime){

        Random random = new Random();

        List<List<Cell>> forestCells = new ArrayList<>();
        

        // get the values of Enum CellState and parse to List.
        List<CellState> cellStates = new ArrayList<>(Arrays.asList(CellState.values()));

        // remove Burning from the list, because i don't want to randomize this value right now
        cellStates.remove(CellState.BURNING);

        for(int i = 0; i < height; i++){
            List<Cell> lineCells = new ArrayList<>();

            for(int j = 0; j < width; j++){
                
                int randomIndex = random.nextInt(cellStates.size());
                double randomMoisture = random.nextDouble(1.0);
                double randomRelief = random.nextDouble(1.0);

                lineCells.add(new Cell(i, j, cellStates.get(randomIndex), randomMoisture, randomRelief));
            }
            forestCells.add(lineCells);
        }

        // now the fire starting point will be set using random index.
        forestCells.get(random.nextInt(height)).get(random.nextInt(width)).startBurning(burningTime);

        List<Direction> windDirections = new ArrayList<>(Arrays.asList(Direction.values()));
        Direction randomWindDirection = windDirections.get(random.nextInt(windDirections.size()));

        this.currentForest = new Forest(height, width, forestCells, randomWindDirection, random.nextDouble(1.0), burningTime, BASE_BURNING_PROBABILITY);
    }

    public void initializeForest(int height, int width, List<List<Cell>> forestCells, 
        Direction windDirection, double windSpeed, int burningTime){
        
        this.currentForest = new Forest(height, width, forestCells, windDirection, 
            windSpeed, burningTime, BASE_BURNING_PROBABILITY);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void calculateNextGeneration(){
        // Uses deep copy constructor to create a safe copy of currentForest
        Forest bufferForest = new Forest(this.currentForest);

        int halfOfCols = (this.currentForest.getCells().get(0).size()) / 2;
        int finalCol = this.currentForest.getCells().get(0).size();

        List<Callable<Forest>> taskList = new ArrayList<>();
        
        // Will organize the tasks at half of each row.
        for(int i = 0; i < this.currentForest.getCells().size(); i++){
            
            // creates 2 tasks for the 2 halves of a row
            SimulationTask taskHalfOfARow = new SimulationTask(this.currentForest, bufferForest, i, 0, halfOfCols);
            SimulationTask taskLastOfARow = new SimulationTask(this.currentForest, bufferForest, i, halfOfCols, finalCol);
            
            taskList.add(taskHalfOfARow);
            taskList.add(taskLastOfARow);
            
        }
        
        try{
            // The code will stop here until the tasks be complete
            List<Future<Forest>> results = executor.invokeAll(taskList);

            for (Future<Forest> f : results) f.get();
            
            this.currentForest = bufferForest;
        } catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
    }
}
