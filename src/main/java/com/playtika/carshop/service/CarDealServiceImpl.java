package com.playtika.carshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import com.playtika.carshop.web.CarNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CarDealServiceImpl implements CarDealService {
    public static final Logger LOG = LoggerFactory.getLogger(CarDealService.class);
    private final AtomicLong id = new AtomicLong();
    private final Map<Long, CarDeal> carDeals = new ConcurrentHashMap<>();

    @Override
    public Long addCarDeal(Car car, String sellerContacts, int price) throws JsonProcessingException {
        CarDeal newCarDeal = new CarDeal();
        newCarDeal.setCar(car);
        newCarDeal.setSaleInfo(new SaleInfo(sellerContacts, price));
        newCarDeal.setId(id.incrementAndGet());
        carDeals.put(newCarDeal.getId(), newCarDeal);
        LOG.info("New deal was added: {}", new ObjectMapper().writeValueAsString(newCarDeal));
        return newCarDeal.getId();
    }

    @Override
    public Collection<CarDeal> getAllCars() {
        return carDeals.values();
    }

    @Override
    public SaleInfo getSaleInfoById(Long id) throws CarNotFoundException {
        if (carDeals.get(id) == null) {
            throw new CarNotFoundException();
        } else {
            return carDeals.get(id).getSaleInfo();
        }
    }

    @Override
    public void deleteCarDealById(Long id) {
        carDeals.remove(id);
    }
}
