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

    // Для поиска с пагинацией
    @Query("SELECT f FROM FlightEntity f WHERE " +
            "(:from IS NULL OR f.route.departureCity.name LIKE %:from%) AND " +
            "(:to IS NULL OR f.route.arrivalCity.name LIKE %:to%) AND " +
            "(:date IS NULL OR DATE(f.departureTime) = :date)")
    Page<FlightEntity> findBySearchCriteria(
            @Param("from") String from,
            @Param("to") String to,
            @Param("date") LocalDate date,
            Pageable pageable
    );

    // Старый метод для обратной совместимости
    @Query("SELECT f FROM FlightEntity f WHERE " +
            "(:from IS NULL OR f.route.departureCity.name LIKE %:from%) AND " +
            "(:to IS NULL OR f.route.arrivalCity.name LIKE %:to%) AND " +
            "(:date IS NULL OR DATE(f.departureTime) = :date)")
    List<FlightEntity> findBySearchCriteria(
            @Param("from") String from,
            @Param("to") String to,
            @Param("date") LocalDate date
    );

    List<FlightEntity> findByRoute_DepartureCity_Name(String departureCity);
    List<FlightEntity> findByRoute_ArrivalCity_Name(String arrivalCity);
}