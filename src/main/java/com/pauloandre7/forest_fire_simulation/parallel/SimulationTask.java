package com.pauloandre7.forest_fire_simulation.parallel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.pauloandre7.forest_fire_simulation.model.BurningCell;
import com.pauloandre7.forest_fire_simulation.model.Cell;
import com.pauloandre7.forest_fire_simulation.model.CellState;
import com.pauloandre7.forest_fire_simulation.model.Direction;
import com.pauloandre7.forest_fire_simulation.model.Forest;


public class SimulationTask implements Callable<Forest>{

    // CurrentForest will be used for all that is related to read data
    private final Forest currentForest;
    // Buffer Forest will only be used to change state, because i cannot change state
    // at the Forest that will be read for other threads.
    private final Forest bufferForest;
    private final int rowOfWork;
    private final int startCol;
    private final int finalCol;
    
    private final double  BONUS_RELIEF = 0.05;    // 5% for each neighbor burning
    private final double  WIND_BONUS_LIMITER = 300; // will stop the maximum wind speed at 33%

    public SimulationTask(Forest originalForest, Forest bufferForest, int rowOfWork, int startCol, int finalCol){
        this.currentForest = originalForest;
        this.bufferForest = bufferForest;
        this.rowOfWork = rowOfWork;
        this. startCol = startCol;
        this.finalCol = finalCol;
    }

    public double calculateIgnitionProbability(int columnIndex) {
        int totalRows = currentForest.getCells().size();
        int totalColumns = currentForest.getCells().get(0).size();

        int currentRowIndex = rowOfWork;
        int currentColumnIndex = columnIndex;

        // When an offSet is added to the current row and col, 
        // we get the coordinate of neighbor's cell.
        final int[][] neighborCoordinates = {
            {-1,  0}, // North (modifies row, modifies col)
            { 1,  0}, // South
            { 0, -1}, // West
            { 0,  1}, // East
            {-1, -1}, // Northwest
            {-1,  1}, // Northeast
            { 1, -1}, // Southwest
            { 1,  1}  // Southeast
        };

        List<Direction> directionsList = new ArrayList<>(Arrays.asList(Direction.values()));

        List<BurningCell> burningCells = new ArrayList<>();

        int count = 0;
        for (int[] offset : neighborCoordinates) {

            // if its the offset 0 (north) and currentRowIndex is 1, then 1+(-1) == 0 (row 0) 
            int neighborRow = currentRowIndex + offset[0];
            // if its the offset north and currentRowIndex is 1, then 1-0 == 1 (row 1 and col 1 == North) 
            int neighborCol = currentColumnIndex + offset[1];

            // Bounds check to avoid accessing outside the grid
            boolean isInsideRows = neighborRow >= 0 && neighborRow < totalRows;
            boolean isInsideColumns = neighborCol >= 0 && neighborCol < totalColumns;
            if (!isInsideRows || !isInsideColumns) {
                count++;
                continue;
            }

            Cell neighborCell = currentForest.getCells().get(neighborRow).get(neighborCol);
            if (neighborCell.getState() == CellState.BURNING) {
                burningCells.add(new BurningCell(neighborCell, directionsList.get(count)));
            }
            count++;
        }

        Cell calculatingCell = currentForest.getCells().get(rowOfWork).get(columnIndex);
        
        // BaseProb will increase with the number of burning cells around
        double baseProb = burningCells.size() * bufferForest.getBaseBurningProbability();
        // Will decrease or maintain the base prob with the moisture level.
        baseProb *= (1 - calculatingCell.getMoisture());
        
        double windProb = 0.0;
        double reliefProb = 0.0;

        for(BurningCell burningCell : burningCells){
            // if the wind is coming from north and one of the burning cell is in the way
            // the wind prob will be considered.
            if(burningCell.getCellDirection() == currentForest.getWindDirection()){
                // If the speed is 100, the limite in 300 will result in 0,33% 
                windProb = currentForest.getWindSpeed() / WIND_BONUS_LIMITER;
            }
            // each burning cell with less relief will add 5% to reliefProb
            if(burningCell.getRelief() < calculatingCell.getRelief()){
                reliefProb += BONUS_RELIEF;
            }
        }

        double ignitionProbability = baseProb + windProb + reliefProb;

        if(ignitionProbability > 1.0) ignitionProbability = 1.0;
        if(ignitionProbability < 0.0) ignitionProbability = 0.0;

        return ignitionProbability;
    }

    @Override
    public Forest call() throws Exception {
        Random random = new Random();

        for(int i = startCol; i < finalCol; i++){
            Cell currentCell = currentForest.getCells().get(rowOfWork).get(i);

            // continue to next cell if this one is Ash, burning or empty.
            if(currentCell.getState() != CellState.VEGETATION) continue;

            double ignitionProbability = calculateIgnitionProbability(i);

            if(random.nextDouble() < ignitionProbability){
                bufferForest.getCells().get(rowOfWork).get(i).startBurning(bufferForest.getBurningTime());
            }
        }

        return bufferForest;
    }
    
}
