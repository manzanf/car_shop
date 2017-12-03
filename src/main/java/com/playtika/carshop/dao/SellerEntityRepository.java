package com.playtika.carshop.dao;

import com.playtika.carshop.dao.entity.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerEntityRepository extends JpaRepository<SellerEntity, Long> {
    SellerEntity findByEmail(String sellerContacts);
}
