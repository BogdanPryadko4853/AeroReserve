package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.AirlineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями авиакомпаний в базе данных.
 * Предоставляет методы для выполнения операций с данными об авиакомпаниях.
 */
@Repository
public interface AirlineRepository extends JpaRepository<AirlineEntity, Long> {

    /**
     * Находит авиакомпанию по названию.
     *
     * @param name название авиакомпании для поиска
     * @return Optional с найденной авиакомпанией или пустой, если не найдена
     */
    Optional<AirlineEntity> findByName(String name);

    /**
     * Находит авиакомпанию по коду.
     *
     * @param code код авиакомпании для поиска
     * @return Optional с найденной авиакомпанией или пустой, если не найдена
     */
    Optional<AirlineEntity> findByCode(String code);

    /**
     * Проверяет существование авиакомпании с указанным названием.
     *
     * @param name название авиакомпании для проверки
     * @return true если авиакомпания с таким названием существует, false в противном случае
     */
    boolean existsByName(String name);

    /**
     * Проверяет существование авиакомпании с указанным кодом.
     *
     * @param code код авиакомпании для проверки
     * @return true если авиакомпания с таким кодом существует, false в противном случае
     */
    boolean existsByCode(String code);
}