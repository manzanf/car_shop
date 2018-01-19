package com.playtika.carshop.service;

import com.playtika.carshop.dao.CarEntityRepository;
import com.playtika.carshop.dao.PurchaseClaimEntityRepository;
import com.playtika.carshop.dao.SaleClaimEntityRepository;
import com.playtika.carshop.dao.SellerEntityRepository;
import com.playtika.carshop.dao.entity.CarEntity;
import com.playtika.carshop.dao.entity.PurchaseClaimEntity;
import com.playtika.carshop.dao.entity.SaleClaimEntity;
import com.playtika.carshop.dao.entity.SellerEntity;
import com.playtika.carshop.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.rmi.NoSuchObjectException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.playtika.carshop.dao.entity.status.PurchaseClaimStatus.accepted;
import static com.playtika.carshop.dao.entity.status.PurchaseClaimStatus.declined;
import static com.playtika.carshop.dao.entity.status.SaleStatus.closed;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
public class CarDealServiceImpl implements CarDealService {
    private final CarEntityRepository carRepository;
    private final SellerEntityRepository sellerRepository;
    private final SaleClaimEntityRepository saleRepository;
    private final PurchaseClaimEntityRepository purchaseRepository;

    CarDealServiceImpl(CarEntityRepository carRepository, SellerEntityRepository sellerRepository, SaleClaimEntityRepository saleRepository, PurchaseClaimEntityRepository purchaseRepository) {
        this.carRepository = carRepository;
        this.sellerRepository = sellerRepository;
        this.saleRepository = saleRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public Long addCarDeal(Car car, String sellerContacts, long price) {
        SellerEntity sellerEntity = addSellerIfNotExists(sellerContacts);
        CarEntity carEntity = addCarIfNotExists(car);
        try {
            return createSaleClaimAndGetId(price, carEntity, sellerEntity);
        } catch (PersistenceException e) {
            log.warn("The car deal was already added for {}, {}", carEntity, e);
            throw new IllegalArgumentException("The car deal was already added for the car");
        }
    }

    @Override
    public Collection<CarDeal> getAllCarDeals() {
        return saleRepository.findAll().stream()
                .map(CarDealServiceImpl::getCarDeal)
                .collect(toList());
    }

    @Override
    public Optional<SaleInfo> getSaleInfoById(Long id) {
        SaleClaimEntity saleClaimEntity = saleRepository.findOne(id);
        if (saleClaimEntity == null) {
            return Optional.empty();
        }
        SaleInfo saleInfo = new SaleInfo(saleClaimEntity.getSeller().getEmail(), saleClaimEntity.getPrice());
        return Optional.of(saleInfo);
    }

    @Override
    public boolean deleteCarDealById(Long id) {
        try {
            saleRepository.delete(id);
            return true;
        } catch (IllegalArgumentException e) {
            log.error("CarDeal {} was not deleted", id);
            return false;
        }
    }

    @Override
    public Long addPurchase(long carDealId, long price) throws NoSuchObjectException {
        SaleClaimEntity saleClaimEntity = saleRepository.findOne(carDealId);
        if (saleClaimEntity == null) {
            log.warn("There is no corresponding car deal with id = {}, purchase claim was not added", carDealId);
            throw new NoSuchObjectException("There is no corresponding car deal, purchase claim was not added");
        }
        PurchaseClaimEntity purchaseClaimEntity = new PurchaseClaimEntity(saleClaimEntity.getCar(), price, saleClaimEntity);
        return purchaseRepository.save(purchaseClaimEntity).getId();
    }

    @Override
    public boolean rejectPurchaseClaim(long id) {
        PurchaseClaimEntity claimEntity = purchaseRepository.findOne(id);
        if (claimEntity == null) {
            log.warn("There is no purchase claim with id = {}", id);
            return false;
        }
        claimEntity.setStatus(declined);
        purchaseRepository.save(claimEntity);
        return true;
    }

    @Override
    public PriceWithState acceptBestPurchaseClaim(long carDealId) {
        SaleClaimEntity sale = saleRepository.findOne(carDealId);
        if (sale == null) {
            log.warn("There is no car deal with id {}", carDealId);
            return new PriceWithState(AcceptBidState.NO_CAR_DEAL);
        }
        if (sale.getStatus() == closed) {
            log.warn("The car deal with id {} was already closed", carDealId);
            return new PriceWithState(AcceptBidState.ALREADY_CLOSED_CAR_DEAL);
        }
//        Optional<PurchaseClaimEntity> maxPricePurchaseClaim = purchaseRepository.findBySaleClaimId(carDealId).stream()
//                .max(comparing(pc -> pc.getPrice()));
        Long price = purchaseRepository.findMaxPriceBySaleClaimId(carDealId);
        if (price == null) {
            log.warn("There are no purchase claims for the car deal with id {}", carDealId);
            return new PriceWithState(AcceptBidState.NO_PURCHASE_CLAIMS);
        }
        sale.setStatus(closed);
        saleRepository.save(sale);
        updateBestPurchaseEntity(carDealId, price);
        rejectOtherPurchaseClaims(carDealId, price);
        return new PriceWithState(price, AcceptBidState.ACCEPTED);
    }

    private void rejectOtherPurchaseClaims(long carDealId, Long price) {
        List<PurchaseClaimEntity> purchaseClaimsToDecline = purchaseRepository.findBySaleClaimIdAndPriceNot(carDealId, price);
        purchaseClaimsToDecline.forEach(p -> {
            p.setStatus(declined);
            purchaseRepository.save(p);
        });
    }

    private void updateBestPurchaseEntity(long carDealId, Long price) {
        PurchaseClaimEntity bestPurchaseClaim = purchaseRepository.findByPriceAndSaleClaimId(price, carDealId);
        bestPurchaseClaim.setStatus(accepted);
        purchaseRepository.save(bestPurchaseClaim);
    }

    private SellerEntity addSellerIfNotExists(String sellerContacts) {
        SellerEntity seller = sellerRepository.findByEmail(sellerContacts);
        if (seller == null) {
            return createSeller(sellerContacts);
        }
        return seller;
    }

    private CarEntity addCarIfNotExists(Car car) {
        CarEntity carEntity = carRepository.findByNumber(car.getModel());
        if (carEntity == null) {
            return createCar(car);
        }
        return carEntity;
    }

    private CarEntity createCar(Car car) {
        CarEntity carEntity = new CarEntity(car.getModel(), car.getColor(), 2016, "BMW");
        return carRepository.save(carEntity);
    }

    private SellerEntity createSeller(String sellerContacts) {
        SellerEntity createdSeller = new SellerEntity(sellerContacts);
        return sellerRepository.save(createdSeller);
    }

    private Long createSaleClaimAndGetId(long price, CarEntity carEntity, SellerEntity sellerEntity) {
        SaleClaimEntity saleClaimEntity = new SaleClaimEntity(carEntity, price, sellerEntity);
        return saleRepository.save(saleClaimEntity).getId();
    }

    private static CarDeal getCarDeal(SaleClaimEntity sc) {
        return new CarDeal(sc.getId(), new SaleInfo(sc.getSeller().getEmail(), sc.getPrice()),
                new Car(sc.getCar().getColor(), sc.getCar().getNumber()));
    }
}
