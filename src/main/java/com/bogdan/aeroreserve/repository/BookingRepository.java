package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями бронирований в базе данных.
 * Предоставляет методы для выполнения операций с данными о бронированиях.
 */
@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    /**
     * Находит все бронирования пользователя.
     *
     * @param user пользователь, чьи бронирования нужно найти
     * @return список бронирований пользователя
     */
    List<BookingEntity> findByUser(UserEntity user);

    /**
     * Находит все бронирования для указанного рейса.
     *
     * @param flight рейс, для которого нужно найти бронирования
     * @return список бронирований для рейса
     */
    List<BookingEntity> findByFlight(FlightEntity flight);

    /**
     * Находит бронирование по номеру бронирования.
     *
     * @param bookingNumber номер бронирования для поиска
     * @return Optional с найденным бронированием или пустой, если не найдено
     */
    Optional<BookingEntity> findByBookingNumber(String bookingNumber);

    /**
     * Проверяет существование активного бронирования для указанного места,
     * исключая бронирования с определенными статусами.
     *
     * @param seatId идентификатор места
     * @param excludedStatuses список статусов, которые следует исключить из проверки
     * @return true если активное бронирование существует, false в противном случае
     */
    @Query("SELECT COUNT(b) > 0 FROM BookingEntity b WHERE b.seat.id = :seatId AND b.status NOT IN :excludedStatuses")
    boolean existsBySeatIdAndStatusNotIn(@Param("seatId") Long seatId,
                                         @Param("excludedStatuses") List<BookingStatus> excludedStatuses);

    /**
     * Находит все бронирования для указанного места,
     * исключая бронирования с определенными статусами.
     *
     * @param seatId идентификатор места
     * @param excludedStatuses список статусов, которые следует исключить
     * @return список бронирований, соответствующих критериям
     */
    @Query("SELECT b FROM BookingEntity b WHERE b.seat.id = :seatId AND b.status NOT IN :excludedStatuses")
    List<BookingEntity> findBySeatIdAndStatusNotIn(@Param("seatId") Long seatId,
                                                   @Param("excludedStatuses") List<BookingStatus> excludedStatuses);

    /**
     * Находит бронирование по идентификатору платежного намерения Stripe.
     *
     * @param paymentIntentId идентификатор платежного намерения Stripe
     * @return Optional с найденным бронированием или пустой, если не найдено
     */
    Optional<BookingEntity> findByPaymentStripePaymentIntentId(String paymentIntentId);
}