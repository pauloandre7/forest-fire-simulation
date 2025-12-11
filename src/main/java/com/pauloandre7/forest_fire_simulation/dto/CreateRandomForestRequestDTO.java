package com.pauloandre7.forest_fire_simulation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateRandomForestRequestDTO{
    private final int height;
    private final int width;
    private final int burningTime;
}