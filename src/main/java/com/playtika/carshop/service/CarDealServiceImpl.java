package com.playtika.carshop.service;

import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CarDealServiceImpl implements CarDealService {
    public static final Logger LOG = LoggerFactory.getLogger(CarDealService.class);
    private final AtomicLong id = new AtomicLong();
    private final Map<Long, CarDeal> carDeals = new ConcurrentHashMap<>();

    @Override
    public Long addCarDeal(Car car, String sellerContacts, int price) {
        CarDeal newCarDeal = new CarDeal();
        newCarDeal.setCar(car);
        newCarDeal.setSaleInfo(new SaleInfo(sellerContacts, price));
        newCarDeal.setId(id.incrementAndGet());
        carDeals.put(newCarDeal.getId(), newCarDeal);
        LOG.info("New deal was added: {}", newCarDeal);
        return newCarDeal.getId();
    }

    @Override
    public Collection<CarDeal> getAllCars() {
        return carDeals.values();
    }

    @Override
    public Optional<SaleInfo> getSaleInfoById(Long id) {
        return Optional.ofNullable(carDeals.get(id)).map(CarDeal::getSaleInfo);
    }

    @Override
    public boolean deleteCarDealById(Long id) {
        return carDeals.remove(id) != null;
    }
}
