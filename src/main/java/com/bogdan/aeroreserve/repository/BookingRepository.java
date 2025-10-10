package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByUser(UserEntity user);
    List<BookingEntity> findByFlight(FlightEntity flight);
    Optional<BookingEntity> findByBookingNumber(String bookingNumber);

    Optional<BookingEntity> findByPaymentStripePaymentIntentId(String paymentIntentId);
}