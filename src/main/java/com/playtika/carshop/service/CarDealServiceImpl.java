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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class CarDealServiceImpl implements CarDealService {
    public static final Logger LOG = LoggerFactory.getLogger(CarDealService.class);

    private final CarEntityRepository carRepository;
    private final SellerEntityRepository sellerRepository;
    private final SaleClaimEntityRepository saleRepository;

    CarDealServiceImpl(CarEntityRepository carRepository, SellerEntityRepository sellerRepository, SaleClaimEntityRepository saleRepository) {
        this.carRepository = carRepository;
        this.sellerRepository = sellerRepository;
        this.saleRepository = saleRepository;
    }

    @Override
    public Long addCarDeal(Car car, String sellerContacts, long price) {
        SellerEntity sellerEntity = addSellerIfNotExists(sellerContacts);
        CarEntity carEntity = addCarIfNotExists(car);
        try {
            return createSaleClaimAndGetId(price, carEntity, sellerEntity);
        } catch (PersistenceException e) {
            LOG.warn("The car deal was already added for {}, {}", carEntity, e);
            throw new IllegalArgumentException("The car deal was already added for the car");
        }
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

    @Override
    public Collection<CarDeal> getAllCarDeals() {
        return saleRepository.findAll().stream()
                .map(CarDealServiceImpl::getCarDeal)
                .collect(toList());
    }

    private static CarDeal getCarDeal(SaleClaimEntity sc) {
        return new CarDeal(sc.getId(), new SaleInfo(sc.getSeller().getEmail(), sc.getPrice()),
                new Car(sc.getCar().getColor(), sc.getCar().getNumber()));
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
            LOG.error("CarDeal {} was not deleted", id);
            return false;
        }
    }
}
