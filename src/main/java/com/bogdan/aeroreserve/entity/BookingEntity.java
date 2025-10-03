package com.bogdan.aeroreserve.entity;

import com.bogdan.aeroreserve.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String bookingNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private FlightEntity flight;

    @OneToOne(mappedBy = "booking")
    private SeatEntity seat;

    private String passengerName;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.CONFIRMED;

    private LocalDateTime bookingDate = LocalDateTime.now();

    public BookingEntity(UserEntity user, FlightEntity flight, SeatEntity seat, String passengerName) {
        this.bookingNumber = generateBookingNumber();
        this.user = user;
        this.flight = flight;
        this.seat = seat;
        this.passengerName = passengerName;
        this.totalPrice = flight.getPrice();
    }

    private String generateBookingNumber() {
        return "AR" + System.currentTimeMillis();
    }
}

