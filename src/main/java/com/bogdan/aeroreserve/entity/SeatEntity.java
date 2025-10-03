package com.bogdan.aeroreserve.entity;

import com.bogdan.aeroreserve.enums.SeatClass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private FlightEntity flight;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatClass seatClass;

    private boolean available = true;

    @OneToOne(mappedBy = "seat")
    private BookingEntity booking;

    public SeatEntity(FlightEntity flight, String seatNumber, SeatClass seatClass) {
        this.flight = flight;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
    }
}

