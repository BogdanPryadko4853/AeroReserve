package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.SeatEntity;
import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.enums.BookingStatus;
import com.bogdan.aeroreserve.repository.BookingRepository;
import com.bogdan.aeroreserve.repository.FlightRepository;
import com.bogdan.aeroreserve.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;

    public BookingEntity createBooking(UserEntity user, Long flightId, String seatNumber, String passengerName) {
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        SeatEntity seat = seatRepository.findByFlightAndSeatNumber(flight, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (!seat.isAvailable()) {
            throw new RuntimeException("Seat is already booked");
        }

        seat.setAvailable(false);
        seatRepository.save(seat);

        BookingEntity booking = new BookingEntity(user, flight, seat, passengerName);
        return bookingRepository.save(booking);
    }

    public List<BookingEntity> getUserBookings(UserEntity user) {
        return bookingRepository.findByUser(user);
    }

    public void cancelBooking(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.getSeat().setAvailable(true);
        seatRepository.save(booking.getSeat());

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public List<BookingEntity> getBookingsByFlight(FlightEntity flight) {
        return bookingRepository.findByFlight(flight);
    }
}