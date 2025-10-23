package com.bogdan.aeroreserve.service.core;

import com.bogdan.aeroreserve.entity.*;
import com.bogdan.aeroreserve.enums.BookingStatus;
import com.bogdan.aeroreserve.repository.BookingRepository;
import com.bogdan.aeroreserve.repository.FlightRepository;
import com.bogdan.aeroreserve.repository.SeatRepository;
import com.bogdan.aeroreserve.service.notification.NotificationService;
import com.bogdan.aeroreserve.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final TicketService ticketService;

    // Статусы, которые считаются активными бронированиями
    private static final List<BookingStatus> ACTIVE_STATUSES = Arrays.asList(
            BookingStatus.PENDING_PAYMENT,
            BookingStatus.CONFIRMED
    );

    /**
     * Создание бронирования с проверкой доступности места
     */
    @Transactional
    public BookingEntity createBooking(UserEntity user, Long flightId, String seatNumber, String passengerName) {
        log.info("Creating booking for user: {}, flight: {}, seat: {}",
                user.getEmail(), flightId, seatNumber);

        // Находим рейс
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + flightId));

        // Находим место
        SeatEntity seat = seatRepository.findByFlightAndSeatNumber(flight, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found: " + seatNumber));

        // Проверяем доступность места
        if (!isSeatAvailable(seat)) {
            throw new RuntimeException("Seat is already booked: " + seatNumber);
        }

        try {
            // Резервируем место
            seat.reserve();
            seatRepository.save(seat);

            // Создаем бронирование
            BookingEntity booking = new BookingEntity(user, flight, seat, passengerName);
            booking = bookingRepository.save(booking);

            // Создаем платеж
            PaymentEntity payment = paymentService.createPaymentIntent(booking);
            booking.setPayment(payment);

            booking = bookingRepository.save(booking);

            log.info("Booking created successfully: {}", booking.getBookingNumber());
            return booking;

        } catch (Exception e) {
            // В случае ошибки освобождаем место
            seat.release();
            seatRepository.save(seat);
            log.error("Failed to create booking", e);
            throw new RuntimeException("Failed to create booking: " + e.getMessage());
        }
    }

    /**
     * Подтверждение бронирования после успешной оплаты
     */
    @Transactional
    public BookingEntity confirmBooking(Long bookingId) {
        log.info("Confirming booking: {}", bookingId);

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (!booking.isPaid()) {
            throw new RuntimeException("Cannot confirm unpaid booking");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);

        // Создаем билет
        TicketEntity ticket = ticketService.createTicket(booking);

        // Отправляем уведомление
        try {
            notificationService.sendBookingConfirmation(booking, ticket);
        } catch (Exception e) {
            log.warn("Failed to send confirmation email: {}", e.getMessage());
        }

        log.info("Booking confirmed: {}", booking.getBookingNumber());
        return booking;
    }

    /**
     * Подтверждение бронирования по paymentIntentId (для webhook)
     */
    @Transactional
    public BookingEntity confirmBookingByPaymentIntent(String paymentIntentId) {
        log.info("Confirming booking by payment intent: {}", paymentIntentId);

        BookingEntity booking = bookingRepository.findByPaymentStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Booking not found for payment intent: " + paymentIntentId));

        return confirmBooking(booking.getId());
    }

    /**
     * Отмена бронирования и освобождение места
     */
    @Transactional
    public void cancelBooking(Long bookingId) {
        log.info("Cancelling booking: {}", bookingId);

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (!booking.canBeCancelled()) {
            throw new RuntimeException("Booking cannot be cancelled in current status: " + booking.getStatus());
        }

        // Отменяем платеж если он не завершен
        if (booking.getPayment() != null && !booking.isPaid()) {
            try {
                paymentService.cancelPayment(booking.getPayment().getStripePaymentIntentId());
            } catch (Exception e) {
                log.warn("Failed to cancel Stripe payment: {}", e.getMessage());
            }
        }

        // Отменяем билет если есть
        ticketService.getTicketByBooking(booking).ifPresent(ticket -> {
            ticketService.cancelTicket(booking);
        });

        // Освобождаем место
        booking.getSeat().release();
        seatRepository.save(booking.getSeat());

        // Обновляем статус бронирования
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Отправляем уведомление
        try {
            notificationService.sendCancellationNotification(booking);
        } catch (Exception e) {
            log.warn("Failed to send cancellation notification: {}", e.getMessage());
        }

        log.info("Booking cancelled: {}", booking.getBookingNumber());
    }

    /**
     * Возврат средств за бронирование
     */
    @Transactional
    public BookingEntity refundBooking(Long bookingId) {
        log.info("Processing refund for booking: {}", bookingId);

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (!booking.canBeRefunded()) {
            throw new RuntimeException("Cannot refund booking in current status");
        }

        if (booking.getPayment() == null) {
            throw new RuntimeException("No payment found for this booking");
        }

        try {
            // Создаем возврат в Stripe
            PaymentEntity refundedPayment = paymentService.createRefund(
                    booking.getPayment().getStripePaymentIntentId()
            );

            // Отменяем билет
            BookingEntity finalBooking = booking;
            ticketService.getTicketByBooking(booking).ifPresent(ticket -> {
                ticketService.cancelTicket(finalBooking);
            });

            // Освобождаем место
            booking.getSeat().release();
            seatRepository.save(booking.getSeat());

            // Обновляем статус
            booking.setStatus(BookingStatus.REFUNDED);
            booking = bookingRepository.save(booking);

            // Отправляем уведомление
            try {
                notificationService.sendRefundNotification(booking);
            } catch (Exception e) {
                log.warn("Failed to send refund notification: {}", e.getMessage());
            }

            log.info("Refund processed successfully for booking: {}", booking.getBookingNumber());
            return booking;

        } catch (Exception e) {
            log.error("Failed to process refund for booking: {}", bookingId, e);
            throw new RuntimeException("Failed to process refund: " + e.getMessage());
        }
    }

    /**
     * Проверка возможности возврата
     */
    public boolean canRefund(BookingEntity booking) {
        return booking.canBeRefunded() &&
                paymentService.canRefund(booking.getPayment().getStripePaymentIntentId());
    }

    /**
     * Проверяет доступность места
     */
    private boolean isSeatAvailable(SeatEntity seat) {
        if (!seat.isAvailableForBooking()) {
            return false;
        }

        // Дополнительная проверка - нет ли активных бронирований для этого места
        return !bookingRepository.existsBySeatIdAndStatusNotIn(
                seat.getId(),
                Arrays.asList(BookingStatus.CANCELLED, BookingStatus.REFUNDED)
        );
    }

    // Методы для получения данных
    public Optional<BookingEntity> getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    public Optional<BookingEntity> findByPaymentIntentId(String paymentIntentId) {
        return bookingRepository.findByPaymentStripePaymentIntentId(paymentIntentId);
    }

    public List<BookingEntity> getBookingsByFlight(FlightEntity flight) {
        return bookingRepository.findByFlight(flight);
    }

    public List<BookingEntity> getUserBookings(UserEntity user) {
        return bookingRepository.findByUser(user);
    }

    public List<BookingEntity> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Получает активные бронирования для места
     */
    public List<BookingEntity> getActiveBookingsForSeat(Long seatId) {
        return bookingRepository.findBySeatIdAndStatusNotIn(seatId,
                Arrays.asList(BookingStatus.CANCELLED, BookingStatus.REFUNDED));
    }
}