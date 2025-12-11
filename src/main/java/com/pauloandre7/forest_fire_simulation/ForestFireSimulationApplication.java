package com.pauloandre7.forest_fire_simulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ForestFireSimulationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForestFireSimulationApplication.class, args);
	}

}
