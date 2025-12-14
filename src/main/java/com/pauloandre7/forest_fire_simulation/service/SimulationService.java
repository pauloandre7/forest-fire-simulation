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

import com.pauloandre7.forest_fire_simulation.dto.CellCoordinatesRequest;
import com.pauloandre7.forest_fire_simulation.dto.CellStateDTO;
import com.pauloandre7.forest_fire_simulation.dto.CurrentForestDTO;
import com.pauloandre7.forest_fire_simulation.dto.CustomForestRequest;
import com.pauloandre7.forest_fire_simulation.dto.RandomForestRequest;
import com.pauloandre7.forest_fire_simulation.exception.EmptyForestException;
import com.pauloandre7.forest_fire_simulation.model.Cell;
import com.pauloandre7.forest_fire_simulation.model.CellState;
import com.pauloandre7.forest_fire_simulation.model.Direction;
import com.pauloandre7.forest_fire_simulation.model.Forest;
import com.pauloandre7.forest_fire_simulation.parallel.SimulationTask;

/**
 * @author pauloandre7
 * 
 * This class holds an instance of Forest and manipulates it to provide responses for controller's 
 * requests.
 * The method to calculate the logic that change state at the cellular automaton uses Tasks and pool of
 * Threads.
 * Furthermore, the method is managed by Scheduler class, that holds an instance of Service and its 
 * managed by SpringBoot. So, to avoid infinite cycles, the service have attributes currentGeneration 
 * and maxGeneration to control the number of cycles.
*/
@Service
public class SimulationService {
    // one cell has 8 neighbors. each neighbor burning will increase base Prob. in 0.12
    private final double BASE_BURNING_PROBABILITY = 0.125;
    private Forest currentForest;
    private final ExecutorService executor;

    // volatile tells jvm to read this variable everytime and avoid synchronization bugs
    private volatile boolean isRunning = false;
    private volatile int currentGeneration;
    // Basically, define the number of cycles for the simulation.
    private volatile int maxGeneration; 

    public SimulationService(){
        // get the amount of available threads and creates a pool for them
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(numberOfThreads);
    }

    public synchronized  void startSimulation(int maxGeneration){
        if(isRunning){
            throw new IllegalStateException("Simulation is already running.");
        }
        isRunning = true;
        this.currentGeneration = 0;
        this.maxGeneration = maxGeneration;
    }

    public synchronized void stopSimulation(){
        isRunning = false;
    }

    public synchronized void iterateGeneration(){
        currentGeneration++;
        if(currentGeneration == maxGeneration){
            stopSimulation();
        }
    }

    // with this method, the sheduler will know the current service status
    public boolean isRunning(){
        return isRunning;
    }

    public CurrentForestDTO getForestForDisplay(){

        // To avoid erros by forest that was not initialized.
        if(this.currentForest == null){
            throw new IllegalStateException("The forest wasn't initialized yet.");
        }

        if(this.currentForest.getCells().isEmpty()){
            throw new EmptyForestException("The forest is empty.");
        }
                
        List<List<CellStateDTO>> grid = new ArrayList<>();
        // This for-structure gets the row from the grid and then gets the cell from the row
        for(List<Cell> row : this.currentForest.getCells()){
            List<CellStateDTO> newRow = new ArrayList<>();
            
            for(Cell cell : row){
                newRow.add(new CellStateDTO(cell.getState()));
            }
            grid.add(newRow);
        }

        return new CurrentForestDTO(grid, this.currentGeneration, this.isRunning);
    }

    public void generateRandomForest(RandomForestRequest randomForestDto){

        if(this.isRunning){
            throw new IllegalStateException("The simulation must be stopped to initialize a new Forest.");
        }

        Random random = new Random();

        List<List<Cell>> forestCells = new ArrayList<>();
        
        // get the values of Enum CellState and parse to List.
        List<CellState> cellStates = new ArrayList<>(Arrays.asList(CellState.values()));

        // remove Burning and Ash from the list, because i don't want to randomize those values right now
        cellStates.remove(CellState.BURNING);
        cellStates.remove(CellState.ASH);

        for(int i = 0; i < randomForestDto.getHeight(); i++){
            List<Cell> lineCells = new ArrayList<>();

            for(int j = 0; j < randomForestDto.getWidth(); j++){
                
                int randomIndex = random.nextInt(cellStates.size());
                double randomMoisture = random.nextDouble(1.0);
                double randomRelief = random.nextDouble(1.0);

                lineCells.add(new Cell(i, j, cellStates.get(randomIndex), randomMoisture, randomRelief));
            }
            forestCells.add(lineCells);
        }

        // now the fire starting point will be set using random index.
        forestCells.get(random.nextInt(randomForestDto.getHeight()))
                    .get(random.nextInt(randomForestDto.getWidth()))
                    .startBurning(randomForestDto.getBurningTime());

        List<Direction> windDirections = new ArrayList<>(Arrays.asList(Direction.values()));
        Direction randomWindDirection = windDirections.get(random.nextInt(windDirections.size()));

        this.currentForest = new Forest(randomForestDto.getHeight(), randomForestDto.getWidth(), 
                                        forestCells, randomWindDirection, random.nextDouble(1.0),
                                        randomForestDto.getBurningTime(), BASE_BURNING_PROBABILITY);
    }

    public void initializeForest(CustomForestRequest initializeForestDto){
        
        if(this.isRunning){
            throw new IllegalStateException("The simulation must be stopped to initialize a new Forest.");
        }

        this.currentForest = new Forest(initializeForestDto.getHeight(), 
                                initializeForestDto.getWidth(), 
                                initializeForestDto.getForestCells(), 
                                initializeForestDto.getWindDirection(), 
                                initializeForestDto.getWindSpeed(), 
                                initializeForestDto.getBurningTime(), 
                                this.BASE_BURNING_PROBABILITY
        );
    }

    public void igniteCell(CellCoordinatesRequest cellCoordinates){
        if(this.currentForest == null){
            throw new IllegalStateException("The forest wasn't initialized yet.");
        }

        if(this.currentForest.getCells().isEmpty()){
            throw new EmptyForestException("The forest is empty.");
        }
        
        this.currentForest.getCells()
            .get(cellCoordinates.getY())
            .get(cellCoordinates.getX())
            .startBurning(currentForest.getBurningTime());
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
