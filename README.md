## Forest Fire Simulation

<p align="center">
	<!-- Badges: substitua pelos badges reais se quiser -->
	<img loading="lazy" src="https://img.shields.io/badge/Status-Development-brightgreen" alt="status"/>
	<img loading="lazy" src="https://img.shields.io/badge/Java-21-orange" alt="java"/>
	<img loading="lazy" src="https://img.shields.io/badge/Spring_Boot-2.7.x-darkgreen" alt="spring"/>
</p>

_REST API to simulate forest fires based on cellular automata and probability._

## Description

This API aims to simulate a forest fire. Using Cellular Automata logic, the forest area is distributed in a matrix, where wach cell represents a portion of that forest and has four possible states: Vegetation, Fire, Ashes and Empty.
The API traverses the matrix with threads and calculates the possibility of ignition based on five variables: moisture, winds direction and speed, Relief and Neighbour Cells. The variables manipulates the ignition probability value and, in the end, a Monte Carlo test is performed using this value.

### Cell States
- **Vegetation:** represents some type of vegetation that serves as fuel for burning;
- **Fire:** state of burning, where the cell can influence the burning of neighboring cells;
- **Ashes:** follows the state of fire, making it impossible for that cell to reignite.
- **Empty space:** represents a portion that does not serve as fuel for burning, such as land, lakes, rocks, etc.


## Functionalities

- Simulation based on cellular automata with probabilistic rules.
- Generation of random and customized forests.
- Parallel/distributed execution of the simulation.
- REST endpoints to control the simulation.

## Technologies used

- **Language**: Java
- **Build**: Maven
- **Framework**: Spring Boot
- **Concurrency**: ExecutorService / parallels tasks

## How to use

1. Build and execution with Maven (dev mode):

```bash
mvn spring-boot:run
```

2. Artifact Build and JAR execution:

```bash
mvn clean package
java -jar target/forest-fire-simulation-0.0.1-SNAPSHOT.jar
```

## License

MIT license Copyright (c) 2025 Paulo André

## Contact

Paulo André — https://github.com/pauloandre7


