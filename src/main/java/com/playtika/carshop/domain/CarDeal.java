package com.playtika.carshop.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDeal {
    @ApiModelProperty(notes = "Car deal id", required = true, example = "1")
    private Long id;

    @ApiModelProperty(notes = "Sale information", required = true)
    private SaleInfo saleInfo;

    @ApiModelProperty(notes = "Car information", required = true)
    private Car car;
}
