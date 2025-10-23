package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.RouteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями рейсов в базе данных.
 * Предоставляет методы для выполнения операций с данными о рейсах,
 * включая сложные поисковые запросы с пагинацией.
 */
public interface FlightRepository extends JpaRepository<FlightEntity, Long> {

    /**
     * Находит рейсы по маршруту и временному интервалу вылета.
     *
     * @param route маршрут рейса
     * @param start начало временного интервала
     * @param end конец временного интервала
     * @return список рейсов, соответствующих критериям
     */
    List<FlightEntity> findByRouteAndDepartureTimeBetween(
            RouteEntity route, LocalDateTime start, LocalDateTime end);

    /**
     * Находит рейсы по городам вылета и прилета и временному интервалу вылета.
     *
     * @param departureCity город вылета
     * @param arrivalCity город прилета
     * @param start начало временного интервала
     * @param end конец временного интервала
     * @return список рейсов, соответствующих критериям
     */
    List<FlightEntity> findByRoute_DepartureCity_NameAndRoute_ArrivalCity_NameAndDepartureTimeBetween(
            String departureCity, String arrivalCity, LocalDateTime start, LocalDateTime end);

    /**
     * Находит рейсы по критериям поиска с пагинацией.
     * Загружает связанные сущности для оптимизации производительности.
     *
     * @param from город вылета (может быть null)
     * @param to город прилета (может быть null)
     * @param date дата вылета
     * @param pageable параметры пагинации
     * @return страница с рейсами, соответствующими критериям поиска
     */
    @EntityGraph(attributePaths = {"route", "route.departureCity", "route.arrivalCity", "aircraft", "airline", "seats"})
    @Query("""
    SELECT f FROM FlightEntity f 
    WHERE (:from IS NULL OR LOWER(f.route.departureCity.name) LIKE CONCAT('%', LOWER(:from), '%')) 
    AND (:to IS NULL OR LOWER(f.route.arrivalCity.name) LIKE CONCAT('%', LOWER(:to), '%')) 
    AND FUNCTION('DATE', f.departureTime) = :date 
    ORDER BY f.departureTime ASC
    """)
    Page<FlightEntity> findBySearchCriteria(
            @Param("from") String from,
            @Param("to") String to,
            @Param("date") LocalDate date,
            Pageable pageable);

    /**
     * Находит рейс по идентификатору с загрузкой всех связанных деталей.
     *
     * @param id идентификатор рейса
     * @return Optional с найденным рейсом или пустой, если не найден
     */
    @EntityGraph(attributePaths = {"route", "route.departureCity", "route.arrivalCity", "aircraft", "airline", "seats"})
    @Query("SELECT f FROM FlightEntity f WHERE f.id = :id")
    Optional<FlightEntity> findByIdWithAllDetails(@Param("id") Long id);

    /**
     * Находит рейсы по городу вылета.
     *
     * @param departureCity город вылета
     * @return список рейсов из указанного города
     */
    List<FlightEntity> findByRoute_DepartureCity_Name(String departureCity);

    /**
     * Находит рейсы по городу прилета.
     *
     * @param arrivalCity город прилета
     * @return список рейсов в указанный город
     */
    List<FlightEntity> findByRoute_ArrivalCity_Name(String arrivalCity);

    /**
     * Находит все рейсы с загрузкой связанных деталей с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница с рейсами и связанными деталями
     */
    @Query("SELECT f FROM FlightEntity f LEFT JOIN FETCH f.route r LEFT JOIN FETCH r.departureCity LEFT JOIN FETCH r.arrivalCity LEFT JOIN FETCH f.aircraft LEFT JOIN FETCH f.airline ORDER BY f.departureTime ASC")
    Page<FlightEntity> findAllWithDetails(Pageable pageable);

    /**
     * Находит рейс по идентификатору с загрузкой информации о местах.
     *
     * @param id идентификатор рейса
     * @return Optional с найденным рейсом или пустой, если не найден
     */
    @Query("SELECT f FROM FlightEntity f LEFT JOIN FETCH f.seats WHERE f.id = :id")
    @EntityGraph(value = "Flight.withAllDetails")
    Optional<FlightEntity> findByIdWithSeats(@Param("id") Long id);
}