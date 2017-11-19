package com.playtika.carshop.web;

import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import com.playtika.carshop.service.CarDealService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        return service.getSaleInfoById(id).orElseThrow(CarNotFoundException::new);
    }

    @DeleteMapping(value = "/{id}")
    public  ResponseEntity<HttpStatus> deleteCarDealById(@PathVariable("id") long id) {
        if (!service.deleteCarDealById(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long addCarDeal(@RequestBody Car car,
                           @RequestParam("price") int price,
                           @RequestParam("sellerContacts") String sellerContacts) {
        return service.addCarDeal(car, sellerContacts, price);
    }
}
