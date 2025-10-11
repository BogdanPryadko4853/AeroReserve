package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.RouteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<FlightEntity, Long> {

    List<FlightEntity> findByRouteAndDepartureTimeBetween(
            RouteEntity route, LocalDateTime start, LocalDateTime end);

    List<FlightEntity> findByRoute_DepartureCity_NameAndRoute_ArrivalCity_NameAndDepartureTimeBetween(
            String departureCity, String arrivalCity, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"route", "route.departureCity", "route.arrivalCity", "aircraft", "airline"})
    @Query("""
        SELECT f FROM FlightEntity f 
        WHERE (:from IS NULL OR LOWER(f.route.departureCity.name) LIKE LOWER(CONCAT('%', :from, '%'))) 
        AND (:to IS NULL OR LOWER(f.route.arrivalCity.name) LIKE LOWER(CONCAT('%', :to, '%'))) 
        AND (:date IS NULL OR FUNCTION('DATE', f.departureTime) = :date) 
        ORDER BY f.departureTime ASC
        """)
    Page<FlightEntity> findBySearchCriteria(
            @Param("from") String from,
            @Param("to") String to,
            @Param("date") LocalDate date,
            Pageable pageable);

    @EntityGraph(attributePaths = {"route", "route.departureCity", "route.arrivalCity", "aircraft", "airline", "seats"})
    @Query("SELECT f FROM FlightEntity f WHERE f.id = :id")
    Optional<FlightEntity> findByIdWithAllDetails(@Param("id") Long id);


    List<FlightEntity> findByRoute_DepartureCity_Name(String departureCity);
    List<FlightEntity> findByRoute_ArrivalCity_Name(String arrivalCity);

    @Query("SELECT f FROM FlightEntity f LEFT JOIN FETCH f.route r LEFT JOIN FETCH r.departureCity LEFT JOIN FETCH r.arrivalCity LEFT JOIN FETCH f.aircraft LEFT JOIN FETCH f.airline ORDER BY f.departureTime ASC")
    Page<FlightEntity> findAllWithDetails(Pageable pageable);
}