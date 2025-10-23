package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями билетов в базе данных.
 * Предоставляет методы для выполнения операций с данными о билетах.
 */
@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    /**
     * Находит билет по бронированию.
     *
     * @param booking бронирование, для которого нужно найти билет
     * @return Optional с найденным билетом или пустой, если не найден
     */
    Optional<TicketEntity> findByBooking(BookingEntity booking);

    /**
     * Находит билет по номеру билета.
     *
     * @param ticketNumber номер билета для поиска
     * @return Optional с найденным билетом или пустой, если не найден
     */
    Optional<TicketEntity> findByTicketNumber(String ticketNumber);
}