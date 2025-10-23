package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями платежей в базе данных.
 * Предоставляет методы для выполнения операций с данными о платежах.
 */
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    /**
     * Находит платеж по идентификатору платежного намерения Stripe.
     *
     * @param stripePaymentIntentId идентификатор платежного намерения Stripe
     * @return Optional с найденным платежом или пустой, если не найден
     */
    Optional<PaymentEntity> findByStripePaymentIntentId(String stripePaymentIntentId);

    /**
     * Находит платеж по идентификатору бронирования.
     *
     * @param bookingId идентификатор бронирования
     * @return Optional с найденным платежом или пустой, если не найден
     */
    Optional<PaymentEntity> findByBookingId(Long bookingId);
}