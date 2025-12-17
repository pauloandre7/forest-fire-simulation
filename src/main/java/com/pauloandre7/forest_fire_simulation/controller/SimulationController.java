package com.pauloandre7.forest_fire_simulation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pauloandre7.forest_fire_simulation.dto.CellCoordinatesRequest;
import com.pauloandre7.forest_fire_simulation.dto.CurrentForestDTO;
import com.pauloandre7.forest_fire_simulation.dto.CustomForestRequest;
import com.pauloandre7.forest_fire_simulation.dto.RandomForestRequest;
import com.pauloandre7.forest_fire_simulation.dto.SimulationStartRequest;
import com.pauloandre7.forest_fire_simulation.exception.EmptyForestException;
import com.pauloandre7.forest_fire_simulation.service.SimulationService;

import lombok.RequiredArgsConstructor;

/**
 * Controller to manage the Fire Forest Simulation
 * <p>
 * This class provides endpoint REST to initialize a Custom Forest,
 * initialize a random forest, to start and stop the simulation, to 
 * burn a specific cell and to get a forest to display.
 * </p>
 *
 * <p>Base URL: {@code /simulation}</p>
 *
 * @author Paulo Andre
 * @version 1.0
 * @since 2025-12-14
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    /**
     * Initialize a custom forest with provided params
     * <p>
     * Needs the required params to fill the DTO class that represents the request.
     * <ul>
     * <li>{@code height} - amount of rows.</li>
     * <li>{@code width} - amount of columns.</li>
     * <li>{@code forestCells} - a grid with configured cells.</li>
     * <li>{@code windDirection} - the direction of the Wind.</li>
     * <li>{@code windSpeed}.</li>
     * <li>{@code burningTime} - the time that a burning cell needs to become ash.</li>
     * </ul>
     * </p>
     *
     * @param initializeForestDto the JSON object containing all the values for a new forest
     * @return {@code ResponseEntity} with confirmation string
     * @throws IllegalStateException if the simulation is already running (returns 204).
     */
    @PostMapping("/forest/custom")
    public ResponseEntity<String> initializeForest(@RequestBody CustomForestRequest initializeForestDto) {
        try{
            simulationService.initializeForest(initializeForestDto);
            return ResponseEntity.ok("The custom forest was created successfully");
            
        }catch(IllegalStateException e){
            return new ResponseEntity<>("Simulation must be stopped to initialize a Forest.", 
                                            HttpStatus.CONFLICT);
        }
    }

    /**
     * Initialize a forest with random configuration
     * <p>
     * Needs 3 basic params to configure a random forest.
     * <ul>
     * <li>{@code height} - amount of rows.</li>
     * <li>{@code width} - amount of columns.</li>
     * <li>{@code burningTime} - the time that a burning cell needs to become ash.</li>
     * </ul>
     * </p>
     *
     * @param randomForestRequest the JSON object cointaining three params for a new forest
     * @return {@code ResponseEntity} with confirmation string
     * @throws IllegalStateException if the simulation is already running (returns 409).
     */
    @PostMapping("/forest/random")
    public ResponseEntity<String> generateRandomForest(@RequestBody RandomForestRequest randomForestRequest){

        try{
            simulationService.generateRandomForest(randomForestRequest);
            return ResponseEntity.ok("Random forest created");

        }catch(IllegalStateException e){
            return new ResponseEntity<>("Simulation must be stopped to create a random Forest.", 
                                            HttpStatus.CONFLICT);
        }
    
    }

    /**
     * Gets a forest with the required data to show the fire spreading
     * <p>
     * Gets the forest with summarized data just to expose the current status
     * of each cell.
     * </p>
     * <p>
     * Forest DTO:
     * <ul>
     * <li>{@code grid} - matrix of summarized cells (only status attribute).</li>
     * <li>{@code currentGeneration} - the current cycle index.</li>
     * <li>{@code isRunning} - the boolean status of simulation execution.</li>
     * </ul>
     * </p>
     * @return A {@link ResponseEntity} that contains the Forest DTO (grid[][], currentGeneration and isRunning) and 200 status (OK).
     * @throws IllegalStateException if the forest wasn't initialized (returns 404).
     * @throws EmptyForestException if the forest is empty (returns 204).
     */
    @GetMapping("/forest")
    public ResponseEntity<CurrentForestDTO> getForestForDisplay(){
        try{
            CurrentForestDTO currentForestDto = simulationService.getForestForDisplay();
            return ResponseEntity.ok(currentForestDto);

        } catch(IllegalStateException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(EmptyForestException e){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Starts the simulation if the forest exists
     * <p>
     * Toggle on the execution status and starts the simulation. Needs a forest to calculate.
     * <ul>
     * <li>{@code maxGeneration} - the amount of cycles of the simulation execution.</li>
     * </ul>
     * </p>
     *
     * @param startDto the JSON object containing maxGeneration param
     * @return {@code ResponseEntity} with confirmation string
     * @throws IllegalStateException if the simulation is already running (returns 409).
     */ 
    @PostMapping("/start")
    public ResponseEntity<String> startSimulation(@RequestBody SimulationStartRequest startDto){
        try{
            simulationService.startSimulation(startDto.getMaxGeneration());
            return ResponseEntity.ok("Simulation started successfully");

        }catch(IllegalStateException e){
            return new ResponseEntity<>("Simulation is already running.", 
                                            HttpStatus.CONFLICT);
        }
    }

    /**
     * Stop the simulation if the forest exists
     * <p> Toggle off the execution status and stop the simulation. <p>
     * 
     * @return {@code ResponseEntity} with confirmation string
     */ 
    @PostMapping("/stop")    
    public ResponseEntity<String> stopSimulation(){
        simulationService.stopSimulation();
        return ResponseEntity.ok("Simulation stopped successfully");
    }

    /**
     * Start fire at a specific cell
     * <p>
     * Needs the coordinates x and y to search the cell and start the fire.
     * <ul>
     * <li>{@code x} - the horizontal coordinate.</li>
     * <li>{@code y} - the vertical coordinate.</li>
     * </ul>
     * </p>
     *
     * @param cellCoordinates the JSON object containing the coordinates
     * @return {@code ResponseEntity} with confirmation string
     * @throws IllegalStateException if the forest wasn't initialized (returns 409).
     * @throws EmptyForestException if the forest is empty (returns 409)
     * @throws IndexOutOfBoundsException if the coordinates exceed the forest limits (returns 400)
     */
    @PostMapping("/forest/ignite")
    public ResponseEntity<String> igniteCell(@RequestBody CellCoordinatesRequest cellCoordinates){
        try{
            simulationService.igniteCell(cellCoordinates);
            return ResponseEntity.ok("Cell burned succesfully.");
            
        } catch(IllegalStateException e){
                return new ResponseEntity<>("The forest must be initialized.", HttpStatus.CONFLICT);
        } catch(EmptyForestException e){
            return new ResponseEntity<>("The forest is empty.", HttpStatus.CONFLICT);
        } catch(IndexOutOfBoundsException e){
            return new ResponseEntity<>("The coordinates doesn't exist.", HttpStatus.BAD_REQUEST);
        }
    }
}