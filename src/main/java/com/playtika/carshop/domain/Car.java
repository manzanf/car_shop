package com.playtika.carshop.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class Car {
    @ApiModelProperty(notes = "Car's color", required = true, example = "red")
    @NonNull
    private String color;

    @ApiModelProperty(notes = "Car's plate number", required = true, example = "GK44FF")
    @NonNull
    private String model;
}
