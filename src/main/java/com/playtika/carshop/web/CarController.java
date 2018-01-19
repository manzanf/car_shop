package com.playtika.carshop.web;

import com.playtika.carshop.domain.*;
import com.playtika.carshop.service.CarDealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.NoSuchObjectException;
import java.util.Collection;

@RestController
@Api(value = "online store", description = "Operations pertaining to cars in Online Store")
@RequestMapping(produces = "application/json")
public class CarController {

    private final CarDealService service;

    public CarController(CarDealService service) {
        this.service = service;
    }

    @GetMapping(value = "/cars")
    @ApiOperation(value = "View a list of all available car deals", response = CarDeal.class, responseContainer = "List")
    public Collection<CarDeal> getAllCars() {
        return service.getAllCarDeals();
    }

    @GetMapping(value = "/cars/{id}")
    @ApiOperation(value = "View the car deal", response = SaleInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Info about the car deal is successfully retrieved"),
            @ApiResponse(code = 404, message = "The car deal you were trying to view is not found")
    })
    public SaleInfo getSaleInfoById(@PathVariable("id") long id) throws CarNotFoundException {
        return service.getSaleInfoById(id).orElseThrow(CarNotFoundException::new);
    }

    @DeleteMapping(value = "/cars/{id}")
    @ApiOperation(value = "Delete the car deal")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The car deal was successfully deleted"),
            @ApiResponse(code = 204, message = "There is no such car deal")
    })
    public ResponseEntity<HttpStatus> deleteCarDealById(@PathVariable("id") long id) {
        if (!service.deleteCarDealById(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/cars", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/purchase")
    @ApiOperation(value = "Add purchase claim for the car deal")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The purchase claim was successfully added"),
            @ApiResponse(code = 204, message = "There is no corresponding car deal, purchase claim was not added")
    })
    public ResponseEntity<Long> addPurchase(@RequestParam("carDealId") long carDealId,
                                            @RequestParam("price") long price) {
        try {
            Long id = service.addPurchase(carDealId, price);
            return new ResponseEntity<>(id, HttpStatus.OK);
        } catch (NoSuchObjectException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @ApiOperation(value = "Reject the purchase claim by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The purchase claim was successfully rejected"),
            @ApiResponse(code = 204, message = "There is no such purchase claim")
    })
    @PostMapping(value = "/purchase/reject")
    public ResponseEntity<HttpStatus> rejectPurchaseClaim(@RequestParam("purchaseClaimId") long id) {
        if (!service.rejectPurchaseClaim(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Accept the best purchase claim for the chosen car deal")
    @GetMapping(value = "/cars/bestBid")
    ClosingCarDealResponse acceptBestPurchaseClaim(@RequestParam("carDealId") long carDealId) {
        PriceWithState priceWithState = service.acceptBestPurchaseClaim(carDealId);
        return new ClosingCarDealResponse(priceWithState.getPrice(), priceWithState.getState().toString());
    }
}
