package com.bogdan.aeroreserve.service.payment;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.PaymentEntity;

public interface PaymentService {

    PaymentEntity createPaymentIntent(BookingEntity booking);

    PaymentEntity confirmPayment(String paymentIntentId);

    String getPaymentStatus(String paymentIntentId);

    PaymentEntity createRefund(String paymentIntentId);

    boolean canRefund(String paymentIntentId);

    PaymentEntity cancelPayment(String paymentIntentId);

}
