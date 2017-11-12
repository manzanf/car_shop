package com.playtika.carshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class SaleInfo {
    @NonNull
    private String sellerContacts;

    private int price;
}
