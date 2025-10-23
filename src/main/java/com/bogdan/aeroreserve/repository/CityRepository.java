package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями городов в базе данных.
 * Предоставляет методы для выполнения операций с данными о городах.
 */
public interface CityRepository extends JpaRepository<CityEntity, Long> {

    /**
     * Находит город по названию.
     *
     * @param name название города для поиска
     * @return Optional с найденным городом или пустой, если не найден
     */
    Optional<CityEntity> findByName(String name);

    /**
     * Находит город по IATA коду.
     *
     * @param iataCode IATA код города для поиска
     * @return Optional с найденным городом или пустой, если не найден
     */
    Optional<CityEntity> findByIataCode(String iataCode);

    /**
     * Находит все города в указанной стране.
     *
     * @param countryName название страны для поиска городов
     * @return список городов в указанной стране
     */
    List<CityEntity> findByCountryName(String countryName);
}