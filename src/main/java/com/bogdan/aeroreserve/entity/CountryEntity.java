package com.bogdan.aeroreserve.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
public class CountryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String code;
    private String currency;
    private String timezone;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<CityEntity> cities = new ArrayList<>();
}