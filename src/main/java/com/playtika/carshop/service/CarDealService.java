package com.playtika.carshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import com.playtika.carshop.web.CarNotFoundException;

import java.util.Collection;

public interface CarDealService {
    Long addCarDeal(Car car, String sellerContacts, int price) throws JsonProcessingException;
    Collection<CarDeal> getAllCars();
    SaleInfo getSaleInfoById(Long id) throws CarNotFoundException;
    void deleteCarDealById(Long id);
}
