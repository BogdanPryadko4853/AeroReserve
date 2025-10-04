package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<FlightEntity, Long> {

    List<FlightEntity> findByRouteAndDepartureTimeBetween(
            RouteEntity route, LocalDateTime start, LocalDateTime end);

    List<FlightEntity> findByRoute_DepartureCity_NameAndRoute_ArrivalCity_NameAndDepartureTimeBetween(
            String departureCity, String arrivalCity, LocalDateTime start, LocalDateTime end);

    List<FlightEntity> findByRoute_DepartureCity_Name(String departureCity);
    List<FlightEntity> findByRoute_ArrivalCity_Name(String arrivalCity);
}