package com.playtika.carshop.dao;

import com.playtika.carshop.dao.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarEntityRepository extends JpaRepository<CarEntity, Long> {
    CarEntity findByNumber(String number);
}
