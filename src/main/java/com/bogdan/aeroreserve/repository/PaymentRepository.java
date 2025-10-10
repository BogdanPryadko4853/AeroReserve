package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByStripePaymentIntentId(String stripePaymentIntentId);
    Optional<PaymentEntity> findByBookingId(Long bookingId);
}
