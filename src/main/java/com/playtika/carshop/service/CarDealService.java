package com.playtika.carshop.service;

import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.PriceWithState;
import com.playtika.carshop.domain.SaleInfo;

import java.rmi.NoSuchObjectException;
import java.util.Collection;
import java.util.Optional;

public interface CarDealService {
    Long addCarDeal(Car car, String sellerContacts, long price);

    Collection<CarDeal> getAllCarDeals();

    Optional<SaleInfo> getSaleInfoById(Long id);

    boolean deleteCarDealById(Long id);

    Long addPurchase(long carDealId, long price) throws NoSuchObjectException;

    boolean rejectPurchaseClaim(long id);

    PriceWithState acceptBestPurchaseClaim(long carDealId);
}
