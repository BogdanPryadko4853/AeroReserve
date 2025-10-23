package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.CityEntity;
import com.bogdan.aeroreserve.entity.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями маршрутов в базе данных.
 * Предоставляет методы для выполнения операций с данными о маршрутах.
 */
public interface RouteRepository extends JpaRepository<RouteEntity, Long> {

    /**
     * Находит маршрут по городам вылета и прилета.
     *
     * @param departure город вылета
     * @param arrival город прилета
     * @return Optional с найденным маршрутом или пустой, если не найден
     */
    Optional<RouteEntity> findByDepartureCityAndArrivalCity(CityEntity departure, CityEntity arrival);

    /**
     * Находит маршруты по названию города вылета.
     *
     * @param departureCity название города вылета
     * @return список маршрутов из указанного города
     */
    List<RouteEntity> findByDepartureCityName(String departureCity);

    /**
     * Находит маршруты по названию города прилета.
     *
     * @param arrivalCity название города прилета
     * @return список маршрутов в указанный город
     */
    List<RouteEntity> findByArrivalCityName(String arrivalCity);
}