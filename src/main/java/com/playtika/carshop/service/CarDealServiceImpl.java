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
import java.util.List;
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
        return createSaleClaimAndGetId(price, carEntity, sellerEntity);
    }

    private SellerEntity addSellerIfNotExists(String sellerContacts) {
        SellerEntity seller;
        try {
            long sellerId = (long) em.createQuery("select s.id from SellerEntity s where s.email = :contacts")
                    .setParameter("contacts", sellerContacts)
                    .getSingleResult();
            seller = em.find(SellerEntity.class, sellerId);
        } catch (NoResultException e) {
            seller = createSeller(sellerContacts);
        }
        return seller;
    }

    private CarEntity addCarIfNotExists(Car car) {
        CarEntity carEntity;
        try {
            long carId = (long) em.createQuery("select c.id from CarEntity c WHERE c.number = :model")
                    .setParameter("model", car.getModel())
                    .getSingleResult();
            carEntity = em.find(CarEntity.class, carId);
        } catch (NoResultException e) {
            carEntity = createCar(car);
        }
        return carEntity;
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
        try {
            SaleClaimEntity saleClaimEntity = new SaleClaimEntity(carEntity, price, sellerEntity);
            em.persist(saleClaimEntity);
            return saleClaimEntity.getId();
        } catch (PersistenceException e) {
            LOG.warn("The car deal was already added {}", e);
            return 0L;
        }
    }

    @Override
    public Collection<CarDeal> getAllCarDeals() {
        List<SaleClaimEntity> saleClaims = em.createQuery("select sc from SaleClaimEntity sc")
                .getResultList();
        return saleClaims.stream()
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
        } else {
            SaleInfo saleInfo = new SaleInfo(saleClaimEntity.getSeller().getEmail(), saleClaimEntity.getPrice());
            return Optional.of(saleInfo);
        }
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
