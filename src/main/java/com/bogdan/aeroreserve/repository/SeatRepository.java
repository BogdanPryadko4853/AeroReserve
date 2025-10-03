package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    List<SeatEntity> findByFlightAndAvailableTrue(FlightEntity flight);
    List<SeatEntity> findByFlight(FlightEntity flight);
    Optional<SeatEntity> findByFlightAndSeatNumber(FlightEntity flight, String seatNumber);
    boolean existsByFlightAndSeatNumber(FlightEntity flight, String seatNumber);
}