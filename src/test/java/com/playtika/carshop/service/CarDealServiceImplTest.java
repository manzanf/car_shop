package com.playtika.carshop.service;

import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class CarDealServiceImplTest {
    private CarDealService service = new CarDealServiceImpl();

    @Test
    public void idIsGeneratedForEveryCarDeal() {
        assertThat(addDealWithPrice(10), is(1L));
        assertThat(addDealWithPrice(20), is(2L));
    }

    @Test
    public void allCarsDealsAreReturned() {
        CarDeal first = new CarDeal(1L, new SaleInfo("tom@gmail.com", 10), new Car("red", "GT23"));
        CarDeal second = new CarDeal(2L, new SaleInfo("tom@gmail.com", 20), new Car("red", "GT23"));
        addDealWithPrice(10);
        addDealWithPrice(20);
        assertThat(service.getAllCars(), hasItems(first, second));
    }

    @Test
    public void IfThereAreNoCarDealsReturnEmptyCollection() {
        assertThat(service.getAllCars(), empty());
    }

    @Test
    public void saleInfoIsReturnedById() {
        addDealWithPrice(10);
        addDealWithPrice(20);
        assertThat(service.getSaleInfoById(2L), is(equalTo(Optional.of(new SaleInfo("tom@gmail.com", 20)))));
    }

    @Test
    public void ifCarDealNotFoundByIdThenReturnEmptyOptional()  {
        assertThat(service.getSaleInfoById(1L), is(equalTo(Optional.empty())));
    }

    @Test
    public void carDealCanBeDeletedById() {
        CarDeal first = new CarDeal(1L, new SaleInfo("tom@gmail.com", 10), new Car("red", "GT23"));
        addDealWithPrice(10);
        addDealWithPrice(20);
        service.deleteCarDealById(2L);
        assertThat(service.getAllCars(), hasItems(first));
        assertThat(service.getAllCars().size(), is(equalTo(1)));
    }

    @Test
    public void ifCarDealIsAbsentThenDeleteDoNothing() {
        service.deleteCarDealById(1L);
        assertThat(service.getAllCars(), empty());
    }

    private long addDealWithPrice(int price) {
        return service.addCarDeal(new Car("red", "GT23"), "tom@gmail.com", price);
    }
}