package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями стран в базе данных.
 * Предоставляет методы для выполнения операций с данными о странах.
 */
public interface CountryRepository extends JpaRepository<CountryEntity, Long> {

    /**
     * Находит страну по коду.
     *
     * @param code код страны для поиска
     * @return Optional с найденной страной или пустой, если не найдена
     */
    Optional<CountryEntity> findByCode(String code);

    /**
     * Находит страну по названию.
     *
     * @param name название страны для поиска
     * @return Optional с найденной страной или пустой, если не найдена
     */
    Optional<CountryEntity> findByName(String name);
}