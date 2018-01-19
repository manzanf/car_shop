package com.playtika.carshop.dao;

import com.playtika.carshop.dao.entity.PurchaseClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseClaimEntityRepository extends JpaRepository<PurchaseClaimEntity, Long> {
    @Query(value = "SELECT MAX(price) FROM purchase_claim WHERE sale_claim_id=?1", nativeQuery = true)
    Long findMaxPriceBySaleClaimId(Long saleClaimId);

    PurchaseClaimEntity findByPriceAndSaleClaimId(Long price, long carDealId);

    List<PurchaseClaimEntity> findBySaleClaimIdAndPriceNot(long carDealId, Long price);
}
