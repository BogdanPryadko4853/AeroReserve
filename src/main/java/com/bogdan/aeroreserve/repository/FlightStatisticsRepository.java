package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.FlightStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы со статистикой рейсов в базе данных.
 * Предоставляет методы для выполнения операций со статистическими данными о рейсах.
 */
@Repository
public interface FlightStatisticsRepository extends JpaRepository<FlightStatisticsEntity, Long> {

    /**
     * Находит статистику по идентификатору рейса.
     *
     * @param flightId идентификатор рейса
     * @return Optional со статистикой рейса или пустой, если не найдена
     */
    Optional<FlightStatisticsEntity> findByFlightId(Long flightId);

    /**
     * Находит всю статистику, отсортированную по дате последнего обновления (по убыванию).
     *
     * @return список статистики рейсов, отсортированный по дате обновления
     */
    List<FlightStatisticsEntity> findAllByOrderByLastUpdatedDesc();

    /**
     * Находит статистику с показателем пунктуальности выше или равным указанному значению.
     *
     * @param minPerformance минимальное значение показателя пунктуальности
     * @return список статистики рейсов, удовлетворяющих критерию
     */
    List<FlightStatisticsEntity> findByOnTimePerformanceGreaterThanEqual(Double minPerformance);
}