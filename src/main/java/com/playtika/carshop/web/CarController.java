package com.playtika.carshop.web;

import com.cedarsoftware.util.io.JsonWriter;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.CarDetails;
import com.playtika.carshop.domain.SaleDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/cars")
public class CarController {
    public static final Logger LOG = LoggerFactory.getLogger(CarController.class);

    private Map<Long, CarDeal> carDeals = new HashMap<>();

    @RequestMapping(method = GET)
    public Collection<CarDeal> getAllCars() {
        return carDeals.values();
    }

    @RequestMapping(value = "/{id}", method = GET)
    public ResponseEntity<SaleDetails> getCarDetailsById(@PathVariable("id") Long id) {
        if (carDeals.get(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(carDeals.get(id).getSaleDetails(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity<HttpStatus> deleteCarById(@PathVariable("id") Long id) {
        if (carDeals.get(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            carDeals.remove(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @RequestMapping(method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> addCar(@RequestBody CarDetails carDetails,
                                       @RequestParam("carPrice") BigDecimal carPrice,
                                       @RequestParam("sellerContacts") String sellerContacts) {
        CarDeal newCarDeal = new CarDeal();
        newCarDeal.setCarDetails(carDetails);
        newCarDeal.setSaleDetails(new SaleDetails(sellerContacts, carPrice));
        newCarDeal.setID(System.currentTimeMillis());
        carDeals.put(newCarDeal.getID(), newCarDeal);
        LOG.info("{}", JsonWriter.toJson(newCarDeal));
        return new ResponseEntity<>(newCarDeal.getID(), HttpStatus.OK);
    }
}
