package com.bogdan.aeroreserve.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "airlines")
@Data
@NoArgsConstructor
public class AirlineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String code; // IATA код, например: "SU", "LH", "AA"

    private String country;

    private String logoUrl;

    @OneToMany(mappedBy = "airline", cascade = CascadeType.ALL)
    private List<FlightEntity> flights = new ArrayList<>();

    public AirlineEntity(String name, String code, String country) {
        this.name = name;
        this.code = code;
        this.country = country;
    }

    public AirlineEntity(String name, String code, String country, String logoUrl) {
        this.name = name;
        this.code = code;
        this.country = country;
        this.logoUrl = logoUrl;
    }
}