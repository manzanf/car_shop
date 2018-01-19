package com.playtika.carshop.dao.entity;

import com.playtika.carshop.dao.entity.status.SaleStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "sale_claim")
@DynamicUpdate
@NoArgsConstructor
public class SaleClaimEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
