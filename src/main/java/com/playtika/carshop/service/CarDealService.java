package com.playtika.carshop.service;

import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;

import java.util.Collection;
import java.util.Optional;

public interface CarDealService {
    Long addCarDeal(Car car, String sellerContacts, int price);

    Collection<CarDeal> getAllCars();

    Optional<SaleInfo> getSaleInfoById(Long id);

    boolean deleteCarDealById(Long id);
}
