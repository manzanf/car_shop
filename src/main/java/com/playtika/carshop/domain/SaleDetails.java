package com.playtika.carshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SaleDetails {
    private String sellerContacts;
    private BigDecimal carPrice;
}
