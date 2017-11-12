package com.playtika.carshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDeal {
    private Long id;
    private SaleInfo saleInfo;
    private Car car;
}
