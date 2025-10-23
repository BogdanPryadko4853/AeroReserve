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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private FlightEntity flight;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatClass seatClass;

    @Column(nullable = false)
    private Boolean available = true;

    /**
     * Помечает место как занятое
     */
    public void reserve() {
        this.available = false;
    }

    /**
     * Освобождает место
     */
    public void release() {
        this.available = true;
    }

    /**
     * Проверяет, доступно ли место для бронирования
     */
    public boolean isAvailableForBooking() {
        return available;
    }
}