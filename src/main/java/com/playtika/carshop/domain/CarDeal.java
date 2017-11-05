package com.playtika.carshop.domain;

import lombok.Data;

@Data
public class CarDeal {
    private Long ID;
    private SaleDetails saleDetails;
    private CarDetails carDetails;
}
