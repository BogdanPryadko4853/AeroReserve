package com.bogdan.aeroreserve.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String stripePaymentIntentId;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private BookingEntity booking;

    private BigDecimal amount;
    private String currency = "USD";
    private String status;
    private String clientSecret;
    private String paymentMethod;
    private String lastPaymentError;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public PaymentEntity(BookingEntity booking, String stripePaymentIntentId, String clientSecret) {
        this.booking = booking;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.clientSecret = clientSecret;
        this.amount = booking.getTotalPrice();
        this.status = "requires_payment_method";
    }
}
