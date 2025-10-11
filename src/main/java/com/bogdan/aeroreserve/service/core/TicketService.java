package com.bogdan.aeroreserve.service.core;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.TicketEntity;
import com.bogdan.aeroreserve.repository.TicketRepository;
import com.bogdan.aeroreserve.service.generator.PdfTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PdfTicketService pdfTicketService;

    /**
     * Создание билета для подтвержденного бронирования
     */
    @Transactional
    public TicketEntity createTicket(BookingEntity booking) {
        // Проверяем, что бронирование подтверждено и оплачено
        if (!booking.isPaid()) {
            throw new RuntimeException("Cannot create ticket for unpaid booking");
        }

        // Проверяем, не существует ли уже билет для этого бронирования
        Optional<TicketEntity> existingTicket = ticketRepository.findByBooking(booking);
        if (existingTicket.isPresent()) {
            log.info("Ticket already exists for booking {}, returning existing ticket", booking.getId());
            return existingTicket.get();
        }

        // Создаем новый билет
        TicketEntity ticket = new TicketEntity();
        ticket.setBooking(booking);
        ticket.setStatus("ISSUED");
        ticket.setBoardingTime(booking.getFlight().getDepartureTime().minusMinutes(45));

        // Номер билета генерируется автоматически в @PrePersist методе

        log.info("Created new ticket for booking {} with ticket number: {}",
                booking.getId(), ticket.getTicketNumber());

        return ticketRepository.save(ticket);
    }

    /**
     * Получение билета по бронированию
     */
    public Optional<TicketEntity> getTicketByBooking(BookingEntity booking) {
        return ticketRepository.findByBooking(booking);
    }

    /**
     * Получение билета по номеру билета
     */
    public Optional<TicketEntity> getTicketByNumber(String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber);
    }

    /**
     * Обновление статуса билета при отмене бронирования
     */
    @Transactional
    public TicketEntity cancelTicket(BookingEntity booking) {
        TicketEntity ticket = ticketRepository.findByBooking(booking)
                .orElseThrow(() -> new RuntimeException("Ticket not found for booking"));

        ticket.setStatus("CANCELLED");
        log.info("Cancelled ticket {} for booking {}", ticket.getTicketNumber(), booking.getId());

        return ticketRepository.save(ticket);
    }

    /**
     * Проверка активности билета
     */
    public boolean isTicketActive(TicketEntity ticket) {
        return "ISSUED".equals(ticket.getStatus()) &&
                ticket.getBooking() != null &&
                ticket.getBooking().isPaid() &&
                ticket.getBooking().getStatus().isConfirmed();
    }

    /**
     * Получение PDF билета
     */
    public byte[] generateTicketPdf(BookingEntity booking) {
        TicketEntity ticket = getTicketByBooking(booking)
                .orElseThrow(() -> new RuntimeException("Ticket not found for booking"));

        return pdfTicketService.generateTicket(booking, ticket);
    }
}