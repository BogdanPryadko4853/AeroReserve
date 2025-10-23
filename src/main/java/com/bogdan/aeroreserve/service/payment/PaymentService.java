package com.bogdan.aeroreserve.service.payment;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.PaymentEntity;

/**
 * Интерфейс платежного сервиса для обработки операций, связанных с оплатой бронирований.
 * Включает функциональность для создания, подтверждения, проверки статуса,
 * возврата и отмены платежей.
 */
public interface PaymentService {

    /**
     * Создает платежное намерение для указанного бронирования.
     *
     * @param booking объект бронирования, для которого создается платеж
     * @return сущность платежа с деталями созданного платежного намерения
     */
    PaymentEntity createPaymentIntent(BookingEntity booking);

    /**
     * Подтверждает платеж по идентификатору платежного намерения.
     *
     * @param paymentIntentId идентификатор платежного намерения
     * @return сущность платежа с обновленным статусом
     */
    PaymentEntity confirmPayment(String paymentIntentId);

    /**
     * Получает текущий статус платежа по идентификатору платежного намерения.
     *
     * @param paymentIntentId идентификатор платежного намерения
     * @return строковое представление статуса платежа
     */
    String getPaymentStatus(String paymentIntentId);

    /**
     * Создает возврат средств для указанного платежного намерения.
     *
     * @param paymentIntentId идентификатор платежного намерения
     * @return сущность платежа с деталями операции возврата
     */
    PaymentEntity createRefund(String paymentIntentId);

    /**
     * Проверяет возможность возврата средств для указанного платежного намерения.
     *
     * @param paymentIntentId идентификатор платежного намерения
     * @return true если возврат возможен, false в противном случае
     */
    boolean canRefund(String paymentIntentId);

    /**
     * Отменяет платеж по указанному идентификатору платежного намерения.
     *
     * @param paymentIntentId идентификатор платежного намерения
     * @return сущность платежа с обновленным статусом отмены
     */
    PaymentEntity cancelPayment(String paymentIntentId);

}