package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.*;
import com.bogdan.aeroreserve.enums.BookingStatus;
import com.bogdan.aeroreserve.repository.BookingRepository;
import com.bogdan.aeroreserve.repository.FlightRepository;
import com.bogdan.aeroreserve.repository.PaymentRepository;
import com.bogdan.aeroreserve.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    /**
     * Создание бронирования с инициализацией платежа
     */
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
    public BookingEntity confirmBooking(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.isPaid()) {
            booking.setStatus(BookingStatus.CONFIRMED);

            try {
                emailService.sendBookingConfirmation(booking);
            } catch (Exception e) {

                System.err.println("Failed to send confirmation email: " + e.getMessage());
            }
            return bookingRepository.save(booking);
        }

        throw new RuntimeException("Booking is not paid");
    }

    /**
     * Отмена бронирования и платежа
     */
    public void cancelBooking(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Если есть платеж, отменяем его
        if (booking.getPayment() != null && !"succeeded".equals(booking.getPayment().getStatus())) {
            stripePaymentService.cancelPayment(booking.getPayment().getStripePaymentIntentId());
        }

        // Освобождаем место
        booking.getSeat().setAvailable(true);
        seatRepository.save(booking.getSeat());

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

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