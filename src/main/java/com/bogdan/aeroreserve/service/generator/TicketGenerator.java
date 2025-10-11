package com.bogdan.aeroreserve.service.generator;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.TicketEntity;

public interface TicketGenerator {

    /**
     * Генерирует билет в указанном формате
     */
    byte[] generateTicket(BookingEntity booking, TicketEntity ticket);

    /**
     * Поддерживаемый формат
     */
    TicketFormat getFormat();

    /**
     * Проверяет поддержку формата
     */
    boolean supports(TicketFormat format);

    enum TicketFormat {
        PDF,
        DOCX,
        CSV,
        HTML,
        IMAGE
    }
}