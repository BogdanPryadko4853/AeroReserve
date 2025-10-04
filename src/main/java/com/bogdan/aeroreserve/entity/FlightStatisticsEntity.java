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
    private Integer availableSeats;
    private Integer occupiedSeats = 0;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private Double loadFactor = 0.0; // процент загрузки

    private Integer cancellations = 0;
    private Integer delays = 0;
    private Integer averageRating = 0;

    @ElementCollection
    @CollectionTable(name = "flight_ratings", joinColumns = @JoinColumn(name = "statistics_id"))
    @Column(name = "rating")
    private List<Integer> ratings = new ArrayList<>();

    public void calculateLoadFactor() {
        if (availableSeats != null && availableSeats > 0) {
            this.loadFactor = (occupiedSeats.doubleValue() / availableSeats) * 100;
        }
    }
}
