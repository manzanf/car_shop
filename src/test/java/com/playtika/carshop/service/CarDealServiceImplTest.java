package com.playtika.carshop.service;

import com.playtika.carshop.dao.CarEntityRepository;
import com.playtika.carshop.dao.PurchaseClaimEntityRepository;
import com.playtika.carshop.dao.SaleClaimEntityRepository;
import com.playtika.carshop.dao.SellerEntityRepository;
import com.playtika.carshop.dao.entity.CarEntity;
import com.playtika.carshop.dao.entity.PurchaseClaimEntity;
import com.playtika.carshop.dao.entity.SaleClaimEntity;
import com.playtika.carshop.dao.entity.SellerEntity;
import com.playtika.carshop.dao.entity.status.SaleStatus;
import com.playtika.carshop.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.rmi.NoSuchObjectException;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
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
    @Mock
    private PurchaseClaimEntityRepository purchaseRepository;

    private Car car = new Car("red", "GT23");
    private CarEntity carEntity = new CarEntity("GT23", "red", 2015, "BMW");
    private SellerEntity sellerEntity = new SellerEntity("tom");
    private long price = 23000L;
    private SaleClaimEntity sale = new SaleClaimEntity(carEntity, price, sellerEntity);
    private PurchaseClaimEntity purchaseEntity = new PurchaseClaimEntity(carEntity, 23000L, sale);

    @Before
    public void setUp() throws Exception {
        service = new CarDealServiceImpl(carRepository, sellerRepository, saleRepository, purchaseRepository);
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
        addDealWithPrice(price);
        when(saleRepository.findOne(2L)).thenReturn(sale);
        assertThat(service.getSaleInfoById(2L), is(equalTo(Optional.of(createSaleInfo(price)))));
    }

    @Test
    public void ifCarDealNotFoundByIdReturnEmptyOptional() {
        when(saleRepository.findOne(1L)).thenReturn(null);
        assertThat(service.getSaleInfoById(1L), is(equalTo(Optional.empty())));
    }

    @Test
    public void carDealCouldBeDeleted() {
        service.deleteCarDealById(2L);
        verify(saleRepository).delete(2L);
    }

    @Test
    public void ifCarDealIsAbsentDeleteDoNothing() {
        doNothing().when(saleRepository).delete(1L);
        assertThat(service.deleteCarDealById(1L), is(equalTo(true)));
    }

    @Test
    public void purchaseClaimCouldBeAdded() throws Exception {
        when(saleRepository.findOne(2L)).thenReturn(sale);
        when(purchaseRepository.save(notNull(PurchaseClaimEntity.class))).thenReturn(purchaseEntity);
        purchaseEntity.setId(1L);
        Long actual = service.addPurchase(2L, price);
        assertThat(actual, is(1L));
    }

    @Test(expected = NoSuchObjectException.class)
    public void ifPurchaseClaimWasNotAddedThrowNoSuchObjectException() throws Exception {
        when(saleRepository.findOne(2L)).thenReturn(null);
        service.addPurchase(2L, price);
    }

    @Test
    public void ifPurchaseClaimWasRejectedReturnTrue() {
        when(purchaseRepository.findOne(1L)).thenReturn(purchaseEntity);
        when(purchaseRepository.save(notNull(PurchaseClaimEntity.class))).thenReturn(purchaseEntity);
        assertThat(service.rejectPurchaseClaim(1L), is(equalTo(true)));
    }

    @Test
    public void ifPurchaseClaimWasNotRejectedReturnFalse() {
        when(purchaseRepository.findOne(1L)).thenReturn(null);
        assertThat(service.rejectPurchaseClaim(1L), is(equalTo(false)));
    }

    @Test
    public void bestBidIsReturnedWithAcceptedState() throws Exception {
        when(saleRepository.findOne(1L)).thenReturn(sale);
        when(purchaseRepository.findMaxPriceBySaleClaimId(1L)).thenReturn(24000L);
        when(purchaseRepository.findByPriceAndSaleClaimId(24000L, 1L)).thenReturn(purchaseEntity);
        when(saleRepository.save(notNull(SaleClaimEntity.class))).thenReturn(sale);
        when(purchaseRepository.save(notNull(PurchaseClaimEntity.class))).thenReturn(purchaseEntity);
        PriceWithState actual = service.acceptBestPurchaseClaim(1L);
        assertThat(actual, is(equalTo(new PriceWithState(24000L, AcceptBidState.ACCEPTED))));
    }

    @Test
    public void ifNoCarDealReturnNoCarDealState() throws Exception {
        when(saleRepository.findOne(1L)).thenReturn(null);
        AcceptBidState state = service.acceptBestPurchaseClaim(1L).getState();
        assertThat(state, is(equalTo(AcceptBidState.NO_CAR_DEAL)));
    }

    @Test
    public void ifCarDealWasClosedReturnAlreadyClosedCarDealState() throws Exception {
        sale.setStatus(SaleStatus.closed);
        when(saleRepository.findOne(1L)).thenReturn(sale);
        AcceptBidState state = service.acceptBestPurchaseClaim(1L).getState();
        assertThat(state, is(equalTo(AcceptBidState.ALREADY_CLOSED_CAR_DEAL)));
    }

    @Test
    public void ifPurchaseClaimsWereNotFoundReturnNoPurchaseClaimsState() throws Exception {
        when(saleRepository.findOne(1L)).thenReturn(sale);
        when(purchaseRepository.findMaxPriceBySaleClaimId(1L)).thenReturn(null);
        AcceptBidState state = service.acceptBestPurchaseClaim(1L).getState();
        assertThat(state, is(equalTo(AcceptBidState.NO_PURCHASE_CLAIMS)));
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