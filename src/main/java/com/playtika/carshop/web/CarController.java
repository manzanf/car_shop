package com.playtika.carshop.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/cars")
public class CarController {
    public static final Logger LOG = LoggerFactory.getLogger(CarController.class);
    private final AtomicLong id = new AtomicLong();
    private final Map<Long, CarDeal> carDeals = new ConcurrentHashMap<>();

    @GetMapping
    public Collection<CarDeal> getAllCars() {
        return carDeals.values();
    }

    @GetMapping(value = "/{id}")
    public SaleInfo getSaleInfoById(@PathVariable("id") Long id) throws CarNotFoundException {
        if (carDeals.get(id) == null) {
            throw new CarNotFoundException();
        } else {
            return carDeals.get(id).getSaleInfo();
        }
    }

    @DeleteMapping(value = "/{id}")
    public void deleteCarDealById(@PathVariable("id") Long id) {
        carDeals.remove(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long addCarDeal(@RequestBody Car car,
                       @RequestParam("price") int price,
                       @RequestParam("sellerContacts") String sellerContacts)
            throws JsonProcessingException {
        CarDeal newCarDeal = new CarDeal();
        newCarDeal.setCar(car);
        newCarDeal.setSaleInfo(new SaleInfo(sellerContacts, price));
        newCarDeal.setId(id.incrementAndGet());
        carDeals.put(newCarDeal.getId(), newCarDeal);
        LOG.info("New deal was added: {}", new ObjectMapper().writeValueAsString(newCarDeal));
        return newCarDeal.getId();
    }
}
