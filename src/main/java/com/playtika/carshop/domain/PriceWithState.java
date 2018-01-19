package com.playtika.carshop.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceWithState {
    private Long price;
    private AcceptBidState state;

    public PriceWithState(AcceptBidState state) {
        this.state = state;
    }
}
