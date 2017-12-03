package com.playtika.carshop.service;

import com.playtika.carshop.dao.CarEntityRepository;
import com.playtika.carshop.dao.SaleClaimEntityRepository;
import com.playtika.carshop.dao.SellerEntityRepository;
import com.playtika.carshop.dao.entity.CarEntity;
import com.playtika.carshop.dao.entity.SaleClaimEntity;
import com.playtika.carshop.dao.entity.SellerEntity;
import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CarDealServiceImplTest {
    private CarDealService service;

    @Mock
    private CarEntityRepository carRepository;
    @Mock
    private SellerEntityRepository sellerRepository;
    @Mock
    private SaleClaimEntityRepository saleRepository;

    private Car car = new Car("red", "GT23");
    private CarEntity carEntity = new CarEntity("GT23", "red", 2015, "BMW");
    private SellerEntity sellerEntity = new SellerEntity("tom");

    @Before
    public void setUp() throws Exception {
        service = new CarDealServiceImpl(carRepository, sellerRepository, saleRepository);
    }

    @Test
    public void carDealCouldBeAddedForNewCarAndSeller() {
        when(carRepository.findByNumber("GT23")).thenReturn(null);
        when(sellerRepository.findByEmail("tom")).thenReturn(null);
        when(carRepository.save(notNull(CarEntity.class))).thenReturn(carEntity);
        when(sellerRepository.save(notNull(SellerEntity.class))).thenReturn(sellerEntity);
        SaleClaimEntity addedClaim = new SaleClaimEntity();
        addedClaim.setId(1L);
        when(saleRepository.save(notNull(SaleClaimEntity.class))).thenReturn(addedClaim);
        Long id = service.addCarDeal(car, "tom", 20L);
        assertThat(id, is(1L));
    }

    @Test
    public void carAndSellerShouldNotBeSavedToDBSecondTime() throws Exception {
        addDealWithPrice(10L);
        verify(carRepository, never()).save(notNull(CarEntity.class));
        verify(sellerRepository, never()).save(notNull(SellerEntity.class));
    }

    @Test
    public void allCarsDealsAreReturned() {
        CarDeal first = new CarDeal(1L, createSaleInfo(10), car);
        CarDeal second = new CarDeal(2L, createSaleInfo(20), car);
        SaleClaimEntity firstClaim = createSaleClaimEntity(10L);
        firstClaim.setId(1L);
        SaleClaimEntity secondClaim = createSaleClaimEntity(20L);
        secondClaim.setId(2L);
        List<SaleClaimEntity> claims = asList(firstClaim, secondClaim);
        when(saleRepository.findAll()).thenReturn(claims);
        addDealWithPrice(10L);
        addDealWithPrice(20L);
        assertThat(service.getAllCarDeals(), hasItems(first, second));
    }

    @Test
    public void IfThereAreNoCarDealsReturnEmptyCollection() {
        when(saleRepository.findAll()).thenReturn(emptyList());
        assertThat(service.getAllCarDeals(), empty());
    }

    @Test
    public void saleInfoIsReturnedById() {
        addDealWithPrice(10L);
        addDealWithPrice(20L);
        when(saleRepository.findOne(2L)).thenReturn(createSaleClaimEntity(20L));
        assertThat(service.getSaleInfoById(2L), is(equalTo(Optional.of(createSaleInfo(20)))));
    }

    @Test
    public void ifCarDealNotFoundByIdThenReturnEmptyOptional() {
        when(saleRepository.findOne(1L)).thenReturn(null);
        assertThat(service.getSaleInfoById(1L), is(equalTo(Optional.empty())));
    }

    @Test
    public void carDealCouldBeDeleted() {
        service.deleteCarDealById(2L);
        verify(saleRepository).delete(2L);
    }

    @Test
    public void ifCarDealIsAbsentThenDeleteDoNothing() {
        doNothing().when(saleRepository).delete(1L);
        assertThat(service.deleteCarDealById(1L), is(equalTo(true)));
    }

    private Long addDealWithPrice(Long price) {
        when(sellerRepository.findByEmail("tom")).thenReturn(sellerEntity);
        when(carRepository.findByNumber("GT23")).thenReturn(carEntity);
        when(saleRepository.save(notNull(SaleClaimEntity.class))).thenReturn(createSaleClaimEntity(price));
        return service.addCarDeal(car, "tom", price);
    }

    private SaleClaimEntity createSaleClaimEntity(long price) {
        return new SaleClaimEntity(carEntity, price, sellerEntity);
    }

    private SaleInfo createSaleInfo(long price) {
        return new SaleInfo("tom", price);
    }
}