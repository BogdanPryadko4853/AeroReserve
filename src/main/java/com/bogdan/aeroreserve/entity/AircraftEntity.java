package com.bogdan.aeroreserve.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aircrafts")
@Data
@NoArgsConstructor
public class AircraftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String model;

    private String manufacturer;

    private int totalSeats;
    private int economySeats;
    private int businessSeats;
    private int firstClassSeats;

    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL)
    private List<FlightEntity> flights = new ArrayList<>();

    public AircraftEntity(String model, String manufacturer, int economySeats, int businessSeats, int firstClassSeats) {
        this.model = model;
        this.manufacturer = manufacturer;
        this.economySeats = economySeats;
        this.businessSeats = businessSeats;
        this.firstClassSeats = firstClassSeats;
        this.totalSeats = economySeats + businessSeats + firstClassSeats;
    }
}