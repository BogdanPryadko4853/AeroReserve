package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями мест в самолете в базе данных.
 * Предоставляет методы для выполнения операций с данными о местах.
 */
@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {

    /**
     * Находит все доступные места для указанного рейса.
     *
     * @param flight рейс, для которого нужно найти доступные места
     * @return список доступных мест
     */
    List<SeatEntity> findByFlightAndAvailableTrue(FlightEntity flight);

    /**
     * Находит все места для указанного рейса.
     *
     * @param flight рейс, для которого нужно найти места
     * @return список всех мест рейса
     */
    List<SeatEntity> findByFlight(FlightEntity flight);

    /**
     * Находит конкретное место по рейсу и номеру места.
     *
     * @param flight рейс
     * @param seatNumber номер места
     * @return Optional с найденным местом или пустой, если не найдено
     */
    Optional<SeatEntity> findByFlightAndSeatNumber(FlightEntity flight, String seatNumber);

    /**
     * Проверяет существование места с указанным номером для рейса.
     *
     * @param flight рейс
     * @param seatNumber номер места
     * @return true если место существует, false в противном случае
     */
    boolean existsByFlightAndSeatNumber(FlightEntity flight, String seatNumber);

    /**
     * Подсчитывает количество доступных мест для рейса по его идентификатору.
     *
     * @param flightId идентификатор рейса
     * @return количество доступных мест
     */
    Integer countByFlightIdAndAvailableTrue(Long flightId);
}