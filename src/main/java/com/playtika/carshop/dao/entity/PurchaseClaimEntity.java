package com.playtika.carshop.dao.entity;

import com.playtika.carshop.dao.entity.status.PurchaseClaimStatus;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
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
}
