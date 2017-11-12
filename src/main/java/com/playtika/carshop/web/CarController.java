package com.playtika.carshop.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import com.playtika.carshop.service.CarDealService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarDealService service;

    public CarController(CarDealService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<CarDeal> getAllCars() {
        return service.getAllCars();
    }

    @GetMapping(value = "/{id}")
    public SaleInfo getSaleInfoById(@PathVariable("id") long id) throws CarNotFoundException {
        return service.getSaleInfoById(id);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteCarDealById(@PathVariable("id") long id) {
        service.deleteCarDealById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long addCarDeal(@RequestBody Car car,
                           @RequestParam("price") int price,
                           @RequestParam("sellerContacts") String sellerContacts)
            throws JsonProcessingException {
        return service.addCarDeal(car, sellerContacts, price);
    }
}
