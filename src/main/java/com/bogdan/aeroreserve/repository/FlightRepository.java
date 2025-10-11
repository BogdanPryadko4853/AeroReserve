package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.RouteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<FlightEntity, Long> {

    List<FlightEntity> findByRouteAndDepartureTimeBetween(
            RouteEntity route, LocalDateTime start, LocalDateTime end);

    List<FlightEntity> findByRoute_DepartureCity_NameAndRoute_ArrivalCity_NameAndDepartureTimeBetween(
            String departureCity, String arrivalCity, LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT f FROM FlightEntity f 
        JOIN f.route r 
        JOIN r.departureCity dc 
        JOIN r.arrivalCity ac 
        WHERE (:from IS NULL OR LOWER(dc.name) LIKE LOWER(CONCAT('%', :from, '%'))) 
        AND (:to IS NULL OR LOWER(ac.name) LIKE LOWER(CONCAT('%', :to, '%'))) 
        AND CAST(f.departureTime AS localdate) = :date 
        ORDER BY f.departureTime ASC
        """)
    Page<FlightEntity> findBySearchCriteria(
            @Param("from") String from,
            @Param("to") String to,
            @Param("date") LocalDate date,
            Pageable pageable);

        @Query("""
        SELECT f FROM FlightEntity f 
        LEFT JOIN FETCH f.route r
        LEFT JOIN FETCH r.departureCity
        LEFT JOIN FETCH r.arrivalCity
        LEFT JOIN FETCH f.aircraft
        LEFT JOIN FETCH f.airline
        ORDER BY f.departureTime ASC
        """)
        Page<FlightEntity> findAllWithDetails(Pageable pageable);


    List<FlightEntity> findByRoute_DepartureCity_Name(String departureCity);
    List<FlightEntity> findByRoute_ArrivalCity_Name(String arrivalCity);
}