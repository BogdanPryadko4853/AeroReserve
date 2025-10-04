package com.bogdan.aeroreserve.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
public class CityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String iataCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private CountryEntity country;

    @OneToMany(mappedBy = "departureCity", cascade = CascadeType.ALL)
    private List<RouteEntity> departureRoutes = new ArrayList<>();

    @OneToMany(mappedBy = "arrivalCity", cascade = CascadeType.ALL)
    private List<RouteEntity> arrivalRoutes = new ArrayList<>();
}
