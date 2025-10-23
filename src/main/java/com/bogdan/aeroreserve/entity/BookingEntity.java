package com.bogdan.aeroreserve.entity;

import com.bogdan.aeroreserve.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"seat_id"})
})
@Data
@NoArgsConstructor
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_number", unique = true, nullable = false)
    private String bookingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private FlightEntity flight;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", unique = true, nullable = false)
    private SeatEntity seat;

    @Column(name = "passenger_name", nullable = false)
    private String passengerName;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PaymentEntity payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING_PAYMENT;

    @CreationTimestamp
    @Column(name = "booking_date", nullable = false, updatable = false)
    private LocalDateTime bookingDate;

    /**
     * Конструктор для создания нового бронирования
     */
    public BookingEntity(UserEntity user, FlightEntity flight, SeatEntity seat, String passengerName) {
        this.bookingNumber = generateBookingNumber();
        this.user = user;
        this.flight = flight;
        this.seat = seat;
        this.passengerName = passengerName;
        this.totalPrice = flight.getPrice();
        this.status = BookingStatus.PENDING_PAYMENT;
    }

    /**
     * Генерация уникального номера бронирования
     */
    private String generateBookingNumber() {
        return "AR" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    /**
     * Проверяет, оплачено ли бронирование
     */
    public boolean isPaid() {
        return payment != null && "succeeded".equals(payment.getStatus());
    }

    /**
     * Проверяет, можно ли отменить бронирование
     */
    public boolean canBeCancelled() {
        return status == BookingStatus.PENDING_PAYMENT ||
                status == BookingStatus.CONFIRMED;
    }

    /**
     * Проверяет, можно ли сделать возврат
     */
    public boolean canBeRefunded() {
        return isPaid() && status != BookingStatus.REFUNDED;
    }
}