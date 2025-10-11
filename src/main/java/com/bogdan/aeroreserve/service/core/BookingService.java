package com.bogdan.aeroreserve.service.core;

import com.bogdan.aeroreserve.entity.*;
import com.bogdan.aeroreserve.enums.BookingStatus;
import com.bogdan.aeroreserve.repository.BookingRepository;
import com.bogdan.aeroreserve.repository.FlightRepository;
import com.bogdan.aeroreserve.repository.SeatRepository;
import com.bogdan.aeroreserve.service.notification.EmailService;
import com.bogdan.aeroreserve.service.notification.NotificationService;
import com.bogdan.aeroreserve.service.payment.PaymentService;
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
    private final PaymentService paymentService;
    private final NotificationService notificationService;
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

        PaymentEntity payment = paymentService.createPaymentIntent(booking);
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

            TicketEntity ticket = ticketService.createTicket(booking);

            try {
                notificationService.sendBookingConfirmation(booking, ticket);
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

        if (booking.getPayment() != null && !"succeeded".equals(booking.getPayment().getStatus())) {
            paymentService.cancelPayment(booking.getPayment().getStripePaymentIntentId());
        }
        ticketService.getTicketByBooking(booking).ifPresent(ticket -> {
            ticketService.cancelTicket(booking);
        });

        booking.getSeat().setAvailable(true);
        seatRepository.save(booking.getSeat());

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        try {
            notificationService.sendCancellationNotification(booking);
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

        if (!booking.isPaid()) {
            throw new RuntimeException("Cannot refund unpaid booking");
        }

        if (booking.getPayment() == null) {
            throw new RuntimeException("No payment found for this booking");
        }

        PaymentEntity refundedPayment = paymentService.createRefund(
                booking.getPayment().getStripePaymentIntentId()
        );

        ticketService.getTicketByBooking(booking).ifPresent(ticket -> {
            ticketService.cancelTicket(booking);
        });

        booking.setStatus(BookingStatus.REFUNDED);

        booking.getSeat().setAvailable(true);
        seatRepository.save(booking.getSeat());

        try {
            notificationService.sendRefundNotification(booking);
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
                paymentService.canRefund(booking.getPayment().getStripePaymentIntentId()) &&
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

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Cannot cancel payment for booking that is not pending payment");
        }

        if (booking.getPayment() != null &&
                !"succeeded".equals(booking.getPayment().getStatus()) &&
                !"canceled".equals(booking.getPayment().getStatus())) {

            try {
                paymentService.cancelPayment(booking.getPayment().getStripePaymentIntentId());
            } catch (Exception e) {
                System.err.println("Failed to cancel Stripe payment: " + e.getMessage());
            }
        }

        booking.getSeat().setAvailable(true);
        seatRepository.save(booking.getSeat());
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        try {
            notificationService.sendCancellationNotification(booking);
        } catch (Exception e) {
            System.err.println("Failed to send cancellation notification: " + e.getMessage());
        }
    }
}