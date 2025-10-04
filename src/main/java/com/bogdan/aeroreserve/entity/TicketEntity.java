package com.bogdan.aeroreserve.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticketNumber;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private BookingEntity booking;

    private String qrCodeUrl;
    private String boardingPassUrl;
    private String status; // ISSUED, BOARDED, USED, CANCELLED

    private LocalDateTime issuedAt = LocalDateTime.now();
    private LocalDateTime boardingTime;

    @PrePersist
    public void generateTicketNumber() {
        this.ticketNumber = "TK" + System.currentTimeMillis();
    }
}
