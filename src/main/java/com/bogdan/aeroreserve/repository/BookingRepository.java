package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByUser(UserEntity user);
    List<BookingEntity> findByFlight(FlightEntity flight);
    Optional<BookingEntity> findByBookingNumber(String bookingNumber);

    @Query("SELECT COUNT(b) > 0 FROM BookingEntity b WHERE b.seat.id = :seatId AND b.status NOT IN :excludedStatuses")
    boolean existsBySeatIdAndStatusNotIn(@Param("seatId") Long seatId,
                                         @Param("excludedStatuses") List<BookingStatus> excludedStatuses);


    @Query("SELECT b FROM BookingEntity b WHERE b.seat.id = :seatId AND b.status NOT IN :excludedStatuses")
    List<BookingEntity> findBySeatIdAndStatusNotIn(@Param("seatId") Long seatId,
                                                   @Param("excludedStatuses") List<BookingStatus> excludedStatuses);

    Optional<BookingEntity> findByPaymentStripePaymentIntentId(String paymentIntentId);
}