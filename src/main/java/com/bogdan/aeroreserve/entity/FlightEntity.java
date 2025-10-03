package com.bogdan.aeroreserve.entity;

import com.bogdan.aeroreserve.enums.FlightStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
public class FlightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String flightNumber;

    private String departureCity;
    private String arrivalCity;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private FlightStatus status = FlightStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private AircraftEntity aircraft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id")
    private AirlineEntity airline;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<SeatEntity> seats = new ArrayList<>();

    public FlightEntity(String flightNumber, String departureCity, String arrivalCity,
                        LocalDateTime departureTime, LocalDateTime arrivalTime,
                        BigDecimal price, AircraftEntity aircraft, AirlineEntity airline) {
        this.flightNumber = flightNumber;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.aircraft = aircraft;
        this.airline = airline;
    }

    public int getAvailableSeats() {
        return (int) seats.stream().filter(SeatEntity::isAvailable).count();
    }

    public String getAircraftModel() {
        return aircraft != null ? aircraft.getModel() : "Unknown";
    }

    public String getAirlineName() {
        return airline != null ? airline.getName() : "Unknown Airline";
    }

    public String getAirlineCode() {
        return airline != null ? airline.getCode() : "N/A";
    }
}