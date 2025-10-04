package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.CityEntity;
import com.bogdan.aeroreserve.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
    Optional<RouteEntity> findByDepartureCityAndArrivalCity(CityEntity departure, CityEntity arrival);
    List<RouteEntity> findByDepartureCityName(String departureCity);
    List<RouteEntity> findByArrivalCityName(String arrivalCity);
}
