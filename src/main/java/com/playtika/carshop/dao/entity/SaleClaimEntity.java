package com.playtika.carshop.dao.entity;

import com.playtika.carshop.dao.entity.status.SaleStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sale_claim")
@Data
@NoArgsConstructor
public class SaleClaimEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private CarEntity car;

    private Long price;

    @ManyToOne
    private SellerEntity seller;

    @Enumerated(EnumType.STRING)
    private SaleStatus status = SaleStatus.open;

    @OneToMany(mappedBy = "saleClaim", cascade = CascadeType.REMOVE)
    private List<PurchaseClaimEntity> purchaseClaims;

    public SaleClaimEntity(CarEntity car, Long price, SellerEntity seller) {
        this.car = car;
        this.price = price;
        this.seller = seller;
    }
}
