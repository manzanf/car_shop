package com.playtika.carshop.dao.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "seller")
@Data
@NoArgsConstructor
public class SellerEntity {
    @Id  @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String email;

    @OneToMany(mappedBy = "seller")
    private List<SaleClaimEntity> saleClaims;

    public SellerEntity(String email) {
        this.email = email;
    }
}
