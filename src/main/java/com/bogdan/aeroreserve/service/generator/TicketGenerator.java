package com.bogdan.aeroreserve.service.generator;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.TicketEntity;

/**
 * Интерфейс для генерации билетов в различных форматах.
 * Определяет контракт для классов, реализующих функциональность создания билетов.
 */
public interface TicketGenerator {

    /**
     * Генерирует билет в указанном формате на основе данных бронирования и билета.
     *
     * @param booking объект бронирования, содержащий информацию о рейсе и пассажире
     * @param ticket объект билета, содержащий детали билета
     * @return массив байтов, представляющий сгенерированный билет в заданном формате
     */
    byte[] generateTicket(BookingEntity booking, TicketEntity ticket);

    /**
     * Возвращает формат билета, который поддерживает данная реализация генератора.
     *
     * @return формат билета из перечисления TicketFormat
     */
    TicketFormat getFormat();

    /**
     * Проверяет, поддерживает ли генератор указанный формат билета.
     *
     * @param format формат билета для проверки
     * @return true если формат поддерживается, false в противном случае
     */
    boolean supports(TicketFormat format);

    /**
     * Перечисление поддерживаемых форматов для генерации билетов.
     */
    enum TicketFormat {
        /** Формат Portable Document Format */
        PDF,
        /** Формат Microsoft Word Document */
        DOCX,
        /** Формат Comma-Separated Values */
        CSV,
        /** Формат HyperText Markup Language */
        HTML,
        /** Формат изображения (например, PNG, JPEG) */
        IMAGE
    }
}