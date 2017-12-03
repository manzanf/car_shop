package com.playtika.carshop.dao.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "seller")
@NoArgsConstructor
public class SellerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    @OneToMany(mappedBy = "seller")
    private List<SaleClaimEntity> saleClaims;

    public SellerEntity(String email) {
        this.email = email;
    }
}
