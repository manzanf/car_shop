package com.playtika.carshop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import com.playtika.carshop.web.CarNotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class CarDealServiceImplTest {
    private CarDealService service;

    @Before
    public void init() throws Exception {
        service = new CarDealServiceImpl();
    }

    @Test
    public void idIsGeneratedForEveryCarDeal() throws Exception {
        assertThat(service.addCarDeal(new Car("red", "GT23"), "tomasMann@gmail.com", 19284), is(1L));
        assertThat(service.addCarDeal(new Car("green", "HN23"), "henrichFitch@gmail.com", 27362), is(2L));
    }

    @Test
    public void allCarsDealsAreReturned() throws Exception {
        CarDeal first = new CarDeal(1L, new SaleInfo("tomasMann@gmail.com", 19284), new Car("red", "GT23"));
        CarDeal second = new CarDeal(2L, new SaleInfo("henrichFitch@gmail.com", 27362), new Car("green", "HN23"));
        addTwoCars();
        assertThat(service.getAllCars(), hasItems(first, second));
    }

    @Test
    public void IfThereAreNoCarDealsReturnEmptyCollection() {
        assertThat(service.getAllCars(), empty());
    }

    @Test
    public void saleInfoIsReturnedById() throws Exception {
        addTwoCars();
        assertThat(service.getSaleInfoById(2L), is(equalTo(new SaleInfo("henrichFitch@gmail.com", 27362))));
    }

    @Test(expected = CarNotFoundException.class)
    public void ifCarDealNotFoundByIdThenThrowException() throws Exception {
        service.getSaleInfoById(1L);
    }

    @Test
    public void carDealCanBeDeletedById() throws Exception {
        CarDeal first = new CarDeal(1L, new SaleInfo("tomasMann@gmail.com", 19284), new Car("red", "GT23"));
        addTwoCars();
        service.deleteCarDealById(2L);
        assertThat(service.getAllCars(), hasItems(first));
        assertThat(service.getAllCars().size(), is(equalTo(1)));
    }

    @Test
    public void ifCarDealIsAbsentThenDeleteDoNothing() throws Exception {
        service.deleteCarDealById(1L);
        assertThat(service.getAllCars(), empty());
    }

    private void addTwoCars() throws JsonProcessingException {
        service.addCarDeal(new Car("red", "GT23"), "tomasMann@gmail.com", 19284);
        service.addCarDeal(new Car("green", "HN23"), "henrichFitch@gmail.com", 27362);
    }
}