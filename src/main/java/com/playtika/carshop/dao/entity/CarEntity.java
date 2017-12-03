package com.playtika.carshop.dao.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "car")
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String number;
    private String color;
    private int year;
    private String brand;

    @OneToMany(mappedBy = "car")
    private List<PurchaseClaimEntity> purchaseClaims;

    @OneToMany(mappedBy = "car")
    private List<SaleClaimEntity> saleClaims;

    public CarEntity(String number, String color, int year, String brand) {
        this.number = number;
        this.color = color;
        this.year = year;
        this.brand = brand;
    }
}
