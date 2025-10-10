package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.PaymentEntity;
import com.bogdan.aeroreserve.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StripePaymentService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @Value("${stripe.currency}")
    private String currency;

    private final PaymentRepository paymentRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Создание платежного намерения для бронирования
     */
    public PaymentEntity createPaymentIntent(BookingEntity booking) {
        try {
            // Конвертируем сумму в центы (Stripe работает в минимальных единицах валюты)
            long amountCents = booking.getTotalPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            Map<String, String> metadata = new HashMap<>();
            metadata.put("booking_id", booking.getId().toString());
            metadata.put("flight_number", booking.getFlight().getFlightNumber());
            metadata.put("passenger_name", booking.getPassengerName());

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountCents)
                    .setCurrency(currency)
                    //.setMetadata(metadata)
                    .setDescription("Flight booking: " + booking.getFlight().getFlightNumber())
                    .putExtraParam("automatic_payment_methods[enabled]", true)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Сохраняем платеж в БД
            PaymentEntity payment = new PaymentEntity(
                    booking,
                    paymentIntent.getId(),
                    paymentIntent.getClientSecret()
            );
            payment.setAmount(booking.getTotalPrice());

            return paymentRepository.save(payment);

        } catch (StripeException e) {
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }


    /**
     * Подтверждение успешного платежа
     */
    public PaymentEntity confirmPayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            PaymentEntity payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            payment.setStatus(paymentIntent.getStatus());
            payment.setPaymentMethod(paymentIntent.getPaymentMethodTypes().get(0));
            payment.setUpdatedAt(java.time.LocalDateTime.now());

            return paymentRepository.save(payment);

        } catch (StripeException e) {
            throw new RuntimeException("Failed to confirm payment: " + e.getMessage(), e);
        }
    }

    /**
     * Получение статуса платежа
     */
    public String getPaymentStatus(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return paymentIntent.getStatus();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to get payment status: " + e.getMessage(), e);
        }
    }

    // В StripePaymentService.java
    /**
     * Создание возврата средств
     */
    public PaymentEntity createRefund(String paymentIntentId) {
        try {
            // Сначала получаем PaymentIntent
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            // Создаем возврат
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();

            Refund refund = Refund.create(params);

            // Обновляем статус платежа
            PaymentEntity payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            payment.setStatus("refunded");
            payment.setUpdatedAt(LocalDateTime.now());

            return paymentRepository.save(payment);

        } catch (StripeException e) {
            throw new RuntimeException("Failed to create refund: " + e.getMessage(), e);
        }
    }

    /**
     * Проверка возможности возврата
     */
    public boolean canRefund(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return "succeeded".equals(paymentIntent.getStatus());
        } catch (StripeException e) {
            return false;
        }
    }

    /**
     * Отмена платежа
     */
    public PaymentEntity cancelPayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            if (!"succeeded".equals(paymentIntent.getStatus())) {
                PaymentIntent canceledIntent = paymentIntent.cancel();

                PaymentEntity payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                        .orElseThrow(() -> new RuntimeException("Payment not found"));

                payment.setStatus(canceledIntent.getStatus());
                payment.setUpdatedAt(java.time.LocalDateTime.now());

                return paymentRepository.save(payment);
            }

            throw new RuntimeException("Cannot cancel succeeded payment");

        } catch (StripeException e) {
            throw new RuntimeException("Failed to cancel payment: " + e.getMessage(), e);
        }
    }
}