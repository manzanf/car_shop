package com.playtika.carshop.dao.entity;

import com.playtika.carshop.dao.entity.status.PurchaseClaimStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@DynamicUpdate
@Table(name = "purchase_claim")
public class PurchaseClaimEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private CarEntity car;

    private Long price;

    @Enumerated(EnumType.STRING)
    private PurchaseClaimStatus status = PurchaseClaimStatus.active;

    @ManyToOne
    private SaleClaimEntity saleClaim;

    public PurchaseClaimEntity(CarEntity car, Long price, SaleClaimEntity saleClaim) {
        this.car = car;
        this.price = price;
        this.saleClaim = saleClaim;
    }
}
