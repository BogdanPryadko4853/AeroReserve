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
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "Flight.withDetails",
                attributeNodes = {
                        @NamedAttributeNode("route"),
                        @NamedAttributeNode(value = "route", subgraph = "routeCities"),
                        @NamedAttributeNode("aircraft"),
                        @NamedAttributeNode("airline")
                },
                subgraphs = {
                        @NamedSubgraph(
                                name = "routeCities",
                                attributeNodes = {
                                        @NamedAttributeNode("departureCity"),
                                        @NamedAttributeNode("arrivalCity")
                                }
                        )
                }
        ),
        @NamedEntityGraph(
                name = "Flight.withAllDetails",
                attributeNodes = {
                        @NamedAttributeNode("route"),
                        @NamedAttributeNode(value = "route", subgraph = "routeCities"),
                        @NamedAttributeNode("aircraft"),
                        @NamedAttributeNode("airline"),
                        @NamedAttributeNode("seats")
                },
                subgraphs = {
                        @NamedSubgraph(
                                name = "routeCities",
                                attributeNodes = {
                                        @NamedAttributeNode("departureCity"),
                                        @NamedAttributeNode("arrivalCity")
                                }
                        )
                }
        )
})
@Data
@NoArgsConstructor
public class FlightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String flightNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private RouteEntity route;

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

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SeatEntity> seats = new ArrayList<>();

    // Добавьте кеширование на уровне метода
    @Transient
    private transient Integer cachedAvailableSeats;

    public int getAvailableSeats() {
        if (cachedAvailableSeats != null) {
            return cachedAvailableSeats;
        }

        if (seats == null || seats.isEmpty()) {
            cachedAvailableSeats = 0;
            return 0;
        }

        cachedAvailableSeats = (int) seats.stream()
                .filter(SeatEntity::isAvailable)
                .count();
        return cachedAvailableSeats;
    }

    public String getDepartureCity() {
        return route != null ? route.getDepartureCity().getName() : "Unknown";
    }

    public String getArrivalCity() {
        return route != null ? route.getArrivalCity().getName() : "Unknown";
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