package com.bogdan.aeroreserve.entity;

import com.bogdan.aeroreserve.entity.FlightEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight_statistics")
@Data
@NoArgsConstructor
public class FlightStatisticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "flight_id")
    private FlightEntity flight;

    private Integer totalBookings = 0;
    private Integer completedFlights = 0;
    private Integer cancelledFlights = 0;

    private Double averageDelayMinutes = 0.0;
    private Double onTimePerformance = 100.0; // в процентах
    private Double customerSatisfactionScore = 0.0; // 1-10

    private Integer totalPassengers = 0;
    private Double loadFactor = 0.0; // процент заполненности

    private LocalDateTime lastUpdated = LocalDateTime.now();

    public FlightStatisticsEntity(FlightEntity flight) {
        this.flight = flight;
    }

    public void calculateLoadFactor() {
        if (flight != null && flight.getAircraft() != null) {
            int totalSeats = flight.getAircraft().getTotalSeats();
            if (totalSeats > 0) {
                this.loadFactor = (double) totalPassengers / totalSeats * 100;
            }
        }
    }
}