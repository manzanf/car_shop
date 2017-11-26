package com.playtika.carshop.service;

import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CarDealServiceImplTest {
    @Autowired
    EntityManager entityManager;

    private CarDealService service;

    @Before
    public void setUp() throws Exception {
        service = new CarDealServiceImpl(entityManager);
   }

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
        assertThat(service.getAllCarDeals(), hasItems(first, second));
    }

    @Test
    public void IfThereAreNoCarDealsReturnEmptyCollection() {
        assertThat(service.getAllCarDeals(), empty());
    }

    @Test
    public void saleInfoIsReturnedById() {
        addDealWithPrice(10);
        addDealWithPrice(20);
        assertThat(service.getSaleInfoById(2L), is(equalTo(Optional.of(new SaleInfo("tom@gmail.com", 20)))));
    }

    @Test
    public void ifCarDealNotFoundByIdThenReturnEmptyOptional() {
        assertThat(service.getSaleInfoById(1L), is(equalTo(Optional.empty())));
    }

    @Test
    public void carDealCanBeDeletedById() {
        CarDeal first = new CarDeal(1L, new SaleInfo("tom@gmail.com", 10), new Car("red", "GT23"));
        addDealWithPrice(10);
        addDealWithPrice(20);
        service.deleteCarDealById(2L);
        assertThat(service.getAllCarDeals(), hasItems(first));
        assertThat(service.getAllCarDeals().size(), is(equalTo(1)));
    }

    @Test
    public void ifCarDealIsAbsentThenDeleteDoNothing() {
        service.deleteCarDealById(1L);
        assertThat(service.getAllCarDeals(), empty());
    }

    private long addDealWithPrice(int price) {
        return service.addCarDeal(new Car("red", "GT23"), "tom@gmail.com", price);
    }
}