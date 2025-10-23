package com.bogdan.aeroreserve.service.notification;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.TicketEntity;

/**
 * Интерфейс службы уведомлений для отправки различных типов уведомлений,
 * связанных с бронированием авиабилетов.
 */
public interface NotificationService {

    /**
     * Отправляет подтверждение бронирования вместе с билетом.
     *
     * @param booking объект бронирования, содержащий информацию о рейсе и пассажире
     * @param ticket объект билета, содержащий детали билета
     */
    void sendBookingConfirmation(BookingEntity booking, TicketEntity ticket);

    /**
     * Отправляет уведомление о возврате средств за бронирование.
     *
     * @param booking объект бронирования, для которого выполняется возврат
     */
    void sendRefundNotification(BookingEntity booking);

    /**
     * Отправляет уведомление об отмене бронирования.
     *
     * @param booking объект отмененного бронирования
     */
    void sendCancellationNotification(BookingEntity booking);
}