package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.AircraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями самолетов в базе данных.
 * Предоставляет методы для выполнения операций с данными о самолетах.
 */
@Repository
public interface AircraftRepository extends JpaRepository<AircraftEntity, Long> {

    /**
     * Находит самолет по модели.
     *
     * @param model модель самолета для поиска
     * @return Optional с найденным самолетом или пустой, если не найден
     */
    Optional<AircraftEntity> findByModel(String model);

    /**
     * Проверяет существование самолета с указанной моделью.
     *
     * @param model модель самолета для проверки
     * @return true если самолет с такой моделью существует, false в противном случае
     */
    boolean existsByModel(String model);
}