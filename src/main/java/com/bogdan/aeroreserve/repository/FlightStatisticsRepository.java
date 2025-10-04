package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.FlightStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightStatisticsRepository extends JpaRepository<FlightStatisticsEntity, Long> {
    Optional<FlightStatisticsEntity> findByFlightId(Long flightId);
    List<FlightStatisticsEntity> findAllByOrderByLastUpdatedDesc();
    List<FlightStatisticsEntity> findByOnTimePerformanceGreaterThanEqual(Double minPerformance);
}