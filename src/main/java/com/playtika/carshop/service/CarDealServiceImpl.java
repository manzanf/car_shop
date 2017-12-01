package com.playtika.carshop.service;

import com.playtika.carshop.dao.entity.CarEntity;
import com.playtika.carshop.dao.entity.SaleClaimEntity;
import com.playtika.carshop.dao.entity.SellerEntity;
import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarDealServiceImpl implements CarDealService {
    public static final Logger LOG = LoggerFactory.getLogger(CarDealService.class);

    @PersistenceContext
    private EntityManager em;

    public CarDealServiceImpl(EntityManager em) {
        this.em = em;
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
        try {
            return em.createQuery("select s from SellerEntity s where s.email = :contacts", SellerEntity.class)
                    .setParameter("contacts", sellerContacts)
                    .getSingleResult();
        } catch (NoResultException e) {
            return createSeller(sellerContacts);
        }

    }

    private CarEntity addCarIfNotExists(Car car) {
        try {
            return em.createQuery("select c from CarEntity c WHERE c.number = :model", CarEntity.class)
                    .setParameter("model", car.getModel())
                    .getSingleResult();
        } catch (NoResultException e) {
            return createCar(car);
        }
    }

    private CarEntity createCar(Car car) {
        CarEntity carEntity = new CarEntity(car.getModel(), car.getColor(), 2016, "BMV");
        em.persist(carEntity);
        return carEntity;
    }

    private SellerEntity createSeller(String sellerContacts) {
        SellerEntity createdSeller = new SellerEntity(sellerContacts);
        em.persist(createdSeller);
        return createdSeller;
    }

    private Long createSaleClaimAndGetId(long price, CarEntity carEntity, SellerEntity sellerEntity) {
        SaleClaimEntity saleClaimEntity = new SaleClaimEntity(carEntity, price, sellerEntity);
        em.persist(saleClaimEntity);
        return saleClaimEntity.getId();
    }

    @Override
    public Collection<CarDeal> getAllCarDeals() {
        return em.createQuery("select sc from SaleClaimEntity sc", SaleClaimEntity.class)
                .getResultList().stream()
                .map(CarDealServiceImpl::getCarDeal)
                .collect(Collectors.toList());
    }

    private static CarDeal getCarDeal(SaleClaimEntity sc) {
        return new CarDeal(sc.getId(), new SaleInfo(sc.getSeller().getEmail(), sc.getPrice()),
                new Car(sc.getCar().getColor(), sc.getCar().getNumber()));
    }


    @Override
    public Optional<SaleInfo> getSaleInfoById(Long id) {
        SaleClaimEntity saleClaimEntity = em.find(SaleClaimEntity.class, id);
        if (saleClaimEntity == null) {
            return Optional.empty();
        }
        SaleInfo saleInfo = new SaleInfo(saleClaimEntity.getSeller().getEmail(), saleClaimEntity.getPrice());
        return Optional.of(saleInfo);
    }

    @Override
    public boolean deleteCarDealById(Long id) {
        int result = em.createQuery("delete from SaleClaimEntity sc where sc.id=:id")
                .setParameter("id", id)
                .executeUpdate();
        // em.remove(em.getReference(SaleClaimEntity.class, id));
        //return result;
        return result == 1;

    }
}
