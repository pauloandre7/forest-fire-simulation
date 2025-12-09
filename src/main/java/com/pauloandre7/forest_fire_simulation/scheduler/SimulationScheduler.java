package com.pauloandre7.forest_fire_simulation.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pauloandre7.forest_fire_simulation.service.SimulationService;

import lombok.RequiredArgsConstructor;


/**
 * @author pauloandre7
 * 
 * This class will be managed by Spring Boot, which will recognize it through 
 * the @component bean and use it to control the calculateNextGeneration()
 * method every 500 ms (time specified in the @Scheduled annotation).
 */
@Component
@RequiredArgsConstructor // Lombok will add the constructor for final's attributes
public class SimulationScheduler {
    
    private final SimulationService service;

    @Scheduled(fixedDelay=500)
    public void tick(){

        if(service.isRunning()){
            service.calculateNextGeneration();
            service.iterateGeneration();
        }
    }
}
