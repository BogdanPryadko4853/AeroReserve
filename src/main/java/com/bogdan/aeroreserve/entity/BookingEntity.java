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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id")
    private FlightEntity flight;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private SeatEntity seat;

    private String passengerName;
    private BigDecimal totalPrice;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PaymentEntity payment;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING_PAYMENT;

    private LocalDateTime bookingDate = LocalDateTime.now();

    public boolean isPaid() {
        return payment != null && "succeeded".equals(payment.getStatus());
    }

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