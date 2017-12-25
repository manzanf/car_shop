package com.playtika.carshop.web;

import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import com.playtika.carshop.service.CarDealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@Api(value = "online store", description = "Operations pertaining to cars in Online Store")
@RequestMapping(value = "/cars", produces = "application/json")
public class CarController {

    private final CarDealService service;

    public CarController(CarDealService service) {
        this.service = service;
    }

    @GetMapping
    @ApiOperation(value = "View a list of all available car deals", response = CarDeal.class)
    public Collection<CarDeal> getAllCars() {
        return service.getAllCarDeals();
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "View the car deal", response = SaleInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Info about the car deal is successfully retrieved"),
            @ApiResponse(code = 404, message = "The car deal you were trying to view is not found")
    })
    public SaleInfo getSaleInfoById(@PathVariable("id") long id) throws CarNotFoundException {
        return service.getSaleInfoById(id).orElseThrow(CarNotFoundException::new);
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete the car deal")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The car deal was successfully deleted"),
            @ApiResponse(code = 204, message = "There are no such car deal")
    })
    public ResponseEntity<HttpStatus> deleteCarDealById(@PathVariable("id") long id) {
        if (!service.deleteCarDealById(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add the new car deal")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The car deal was successfully added"),
            @ApiResponse(code = 204, message = "The car deal was already added for the car")
    })
    public ResponseEntity<Long> addCarDeal(@RequestBody Car car,
                                           @RequestParam("price") long price,
                                           @RequestParam("sellerContacts") String sellerContacts) {
        try {
            Long id = service.addCarDeal(car, sellerContacts, price);
            return new ResponseEntity<>(id, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
