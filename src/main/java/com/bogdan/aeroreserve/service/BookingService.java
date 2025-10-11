package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.*;
import com.bogdan.aeroreserve.enums.BookingStatus;
import com.bogdan.aeroreserve.repository.BookingRepository;
import com.bogdan.aeroreserve.repository.FlightRepository;
import com.bogdan.aeroreserve.repository.PaymentRepository;
import com.bogdan.aeroreserve.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;
    private final StripePaymentService stripePaymentService;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;
    private final TicketService ticketService;

    /**
     * Создание бронирования с инициализацией платежа
     */
    @Transactional
    public BookingEntity createBooking(UserEntity user, Long flightId, String seatNumber, String passengerName) {
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        SeatEntity seat = seatRepository.findByFlightAndSeatNumber(flight, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (!seat.isAvailable()) {
            throw new RuntimeException("Seat is already booked");
        }

        // Временно резервируем место
        seat.setAvailable(false);
        seatRepository.save(seat);

        // Создаем бронирование
        BookingEntity booking = new BookingEntity(user, flight, seat, passengerName);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking = bookingRepository.save(booking);

        // Создаем платежное намерение в Stripe
        PaymentEntity payment = stripePaymentService.createPaymentIntent(booking);
        booking.setPayment(payment);

        return bookingRepository.save(booking);
    }

    /**
     * Подтверждение бронирования после успешной оплаты
     */
    @Transactional
    public BookingEntity confirmBooking(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.isPaid()) {
            booking.setStatus(BookingStatus.CONFIRMED);
            booking = bookingRepository.save(booking);

            // СОЗДАЕМ БИЛЕТ после подтверждения оплаты
            TicketEntity ticket = ticketService.createTicket(booking);

            try {
                // Отправляем email с подтверждением и информацией о билете
                emailService.sendBookingConfirmation(booking, ticket);
            } catch (Exception e) {
                System.err.println("Failed to send confirmation email: " + e.getMessage());
            }
            return booking;
        }

        throw new RuntimeException("Booking is not paid");
    }

    /**
     * Подтверждение бронирования по paymentIntentId (для webhook)
     */
    @Transactional
    public BookingEntity confirmBookingByPaymentIntent(String paymentIntentId) {
        BookingEntity booking = bookingRepository.findByPaymentStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Booking not found for payment intent: " + paymentIntentId));

        return confirmBooking(booking.getId());
    }

    /**
     * Отмена бронирования и платежа
     */
    @Transactional
    public void cancelBooking(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Если есть платеж, отменяем его
        if (booking.getPayment() != null && !"succeeded".equals(booking.getPayment().getStatus())) {
            stripePaymentService.cancelPayment(booking.getPayment().getStripePaymentIntentId());
        }

        // ОТМЕНЯЕМ БИЛЕТ если он был создан
        ticketService.getTicketByBooking(booking).ifPresent(ticket -> {
            ticketService.cancelTicket(booking);
        });

        // Освобождаем место
        booking.getSeat().setAvailable(true);
        seatRepository.save(booking.getSeat());

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        try {
            emailService.sendCancellationNotification(booking);
        } catch (Exception e) {
            System.err.println("Failed to send cancellation notification: " + e.getMessage());
        }
    }

    /**
     * Возврат средств за бронирование
     */
    @Transactional
    public BookingEntity refundBooking(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Проверяем, что бронирование оплачено
        if (!booking.isPaid()) {
            throw new RuntimeException("Cannot refund unpaid booking");
        }

        // Проверяем, что есть платеж
        if (booking.getPayment() == null) {
            throw new RuntimeException("No payment found for this booking");
        }

        // Создаем возврат в Stripe
        PaymentEntity refundedPayment = stripePaymentService.createRefund(
                booking.getPayment().getStripePaymentIntentId()
        );

        // ОТМЕНЯЕМ БИЛЕТ при возврате
        ticketService.getTicketByBooking(booking).ifPresent(ticket -> {
            ticketService.cancelTicket(booking);
        });

        // Обновляем статус бронирования
        booking.setStatus(BookingStatus.REFUNDED);

        // Освобождаем место
        booking.getSeat().setAvailable(true);
        seatRepository.save(booking.getSeat());

        try {
            emailService.sendRefundNotification(booking);
        } catch (Exception e) {
            System.err.println("Failed to send refund notification: " + e.getMessage());
        }

        return bookingRepository.save(booking);
    }

    /**
     * Проверка возможности возврата
     */
    public boolean canRefund(BookingEntity booking) {
        return booking.isPaid() &&
                booking.getPayment() != null &&
                stripePaymentService.canRefund(booking.getPayment().getStripePaymentIntentId()) &&
                booking.getStatus() != BookingStatus.REFUNDED;
    }

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

    /**
     * Отмена платежа и бронирования (для случаев, когда оплата не прошла)
     */
    @Transactional
    public void cancelPaymentAndBooking(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Проверяем, что бронирование ожидает оплаты
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Cannot cancel payment for booking that is not pending payment");
        }

        // Если есть платеж в Stripe, отменяем его
        if (booking.getPayment() != null &&
                !"succeeded".equals(booking.getPayment().getStatus()) &&
                !"canceled".equals(booking.getPayment().getStatus())) {

            try {
                stripePaymentService.cancelPayment(booking.getPayment().getStripePaymentIntentId());
            } catch (Exception e) {
                // Логируем ошибку, но продолжаем отмену бронирования
                System.err.println("Failed to cancel Stripe payment: " + e.getMessage());
            }
        }

        // Освобождаем место
        booking.getSeat().setAvailable(true);
        seatRepository.save(booking.getSeat());

        // Обновляем статус бронирования
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        try {
            emailService.sendCancellationNotification(booking);
        } catch (Exception e) {
            System.err.println("Failed to send cancellation notification: " + e.getMessage());
        }
    }
}