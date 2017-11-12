package com.playtika.carshop.domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class Car {
    @NonNull
    private String color;

    @NonNull
    private String model;
}
