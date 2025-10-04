package com.bogdan.aeroreserve.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
public class RouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_city_id")
    private CityEntity departureCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_city_id")
    private CityEntity arrivalCity;

    private BigDecimal basePrice;
    private Integer averageDuration;
    private Integer distance;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<FlightEntity> flights = new ArrayList<>();
}