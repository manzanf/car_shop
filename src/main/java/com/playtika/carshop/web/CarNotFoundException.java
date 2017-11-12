package com.playtika.carshop.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Car not found")
public class CarNotFoundException extends Exception {
    public CarNotFoundException() {
        super("CarNotFoundException");
    }
}