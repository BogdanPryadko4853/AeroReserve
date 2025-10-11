package com.bogdan.aeroreserve.service.notification;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.TicketEntity;

public interface NotificationService {
    void sendBookingConfirmation(BookingEntity booking, TicketEntity ticket);

    void sendRefundNotification(BookingEntity booking);

    void sendCancellationNotification(BookingEntity booking);
}
