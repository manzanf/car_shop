package com.playtika.carshop.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class SaleInfo {
    @ApiModelProperty(notes = "Seller's contacts", required = true, example = "tom@gmail.com")
    @NonNull
    private String sellerContacts;

    @ApiModelProperty(notes = "Price of the car", required = true, example = "23000")
    private long price;
}
