package com.bogdan.aeroreserve.service.core;

import com.bogdan.aeroreserve.entity.*;
import com.bogdan.aeroreserve.enums.FlightStatus;
import com.bogdan.aeroreserve.repository.FlightRepository;
import com.bogdan.aeroreserve.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"flights", "seats"})
public class FlightService {
    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final RouteService routeService;
    private final CityService cityService;

    // Кеширование всех рейсов с пагинацией
    @Cacheable(value = "flights", key = "'all-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    @Transactional(readOnly = true)
    public Page<FlightEntity> getAllFlights(Pageable pageable) {
        log.info("Loading all flights from database, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return flightRepository.findAllWithDetails(pageable);
    }

    // Кеширование поиска рейсов
    @Cacheable(value = "flights", key = "'search-' + #from + '-' + #to + '-' + #date + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<FlightEntity> searchFlights(String from, String to, LocalDate date, Pageable pageable) {
        log.info("Searching flights from database: from={}, to={}, date={}, page={}, size={}",
                from, to, date, pageable.getPageNumber(), pageable.getPageSize());

        // Нормализуем параметры
        String normalizedFrom = (from != null && !from.trim().isEmpty()) ? from.trim() : null;
        String normalizedTo = (to != null && !to.trim().isEmpty()) ? to.trim() : null;
        LocalDate normalizedDate = date;

        // Если все параметры пустые, возвращаем все рейсы
        if (normalizedFrom == null && normalizedTo == null && normalizedDate == null) {
            return getAllFlights(pageable);
        }

        return flightRepository.findBySearchCriteria(normalizedFrom, normalizedTo, normalizedDate, pageable);
    }

    // Кеширование рейса по ID с полными деталями
    @Cacheable(value = "flights", key = "'detail-' + #id")
    @Transactional(readOnly = true)
    public Optional<FlightEntity> getFlightById(Long id) {
        log.info("Loading flight details from database, id: {}", id);
        return flightRepository.findByIdWithAllDetails(id);
    }

    // Кеширование простого рейса по ID (без деталей)
    @Cacheable(value = "flights", key = "'simple-' + #id")
    @Transactional(readOnly = true)
    public Optional<FlightEntity> getSimpleFlightById(Long id) {
        log.info("Loading simple flight from database, id: {}", id);
        return flightRepository.findById(id);
    }

    // Кеширование доступных мест
    @Cacheable(value = "seats", key = "'available-' + #flightId")
    @Transactional(readOnly = true)
    public List<SeatEntity> getAvailableSeats(Long flightId) {
        log.info("Loading available seats from database for flight: {}", flightId);
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return seatRepository.findByFlightAndAvailableTrue(flight);
    }

    // Кеширование количества доступных мест
    @Cacheable(value = "seats", key = "'count-' + #flightId")
    @Transactional(readOnly = true)
    public Integer getAvailableSeatsCount(Long flightId) {
        log.info("Counting available seats from database for flight: {}", flightId);
        return seatRepository.countByFlightIdAndAvailableTrue(flightId);
    }

    // Старые методы поиска (также кешируем)
    @Cacheable(value = "flights", key = "'old-search-' + #from + '-' + #to + '-' + #date")
    @Transactional(readOnly = true)
    public List<FlightEntity> searchFlights(String from, String to, LocalDate date) {
        log.info("Executing old search: from={}, to={}, date={}", from, to, date);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        CityEntity departureCity = cityService.getCityByName(from)
                .orElseThrow(() -> new RuntimeException("Departure city not found: " + from));
        CityEntity arrivalCity = cityService.getCityByName(to)
                .orElseThrow(() -> new RuntimeException("Arrival city not found: " + to));

        RouteEntity route = routeService.getRouteByCities(departureCity, arrivalCity)
                .orElseThrow(() -> new RuntimeException("Route not found between " + from + " and " + to));

        return flightRepository.findByRouteAndDepartureTimeBetween(route, start, end);
    }

    @Cacheable(value = "flights", key = "'city-search-' + #from + '-' + #to + '-' + #date")
    @Transactional(readOnly = true)
    public List<FlightEntity> searchFlightsByCityNames(String from, String to, LocalDate date) {
        log.info("Executing city name search: from={}, to={}, date={}", from, to, date);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return flightRepository.findByRoute_DepartureCity_NameAndRoute_ArrivalCity_NameAndDepartureTimeBetween(
                from, to, start, end);
    }

    // Методы создания/обновления с инвалидацией кеша
    @Caching(evict = {
            @CacheEvict(value = "flights", allEntries = true),
            @CacheEvict(value = "seats", allEntries = true)
    })
    @Transactional
    public FlightEntity createFlight(String flightNumber, RouteEntity route,
                                     LocalDateTime departureTime, LocalDateTime arrivalTime,
                                     BigDecimal price, AircraftEntity aircraft, AirlineEntity airline) {
        log.info("Creating new flight: {}", flightNumber);

        FlightEntity flight = new FlightEntity();
        flight.setFlightNumber(flightNumber);
        flight.setRoute(route);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setPrice(price);
        flight.setAircraft(aircraft);
        flight.setAirline(airline);
        flight.setStatus(FlightStatus.SCHEDULED);

        return flightRepository.save(flight);
    }

    @Caching(evict = {
            @CacheEvict(value = "flights", allEntries = true),
            @CacheEvict(value = "seats", allEntries = true)
    })
    @Transactional
    public FlightEntity createFlightWithCities(String flightNumber, String departureCityName,
                                               String arrivalCityName, LocalDateTime departureTime,
                                               LocalDateTime arrivalTime, BigDecimal price,
                                               AircraftEntity aircraft, AirlineEntity airline) {
        log.info("Creating new flight with cities: {} -> {}", departureCityName, arrivalCityName);

        CityEntity departureCity = cityService.getCityByName(departureCityName)
                .orElseThrow(() -> new RuntimeException("Departure city not found: " + departureCityName));
        CityEntity arrivalCity = cityService.getCityByName(arrivalCityName)
                .orElseThrow(() -> new RuntimeException("Arrival city not found: " + arrivalCityName));

        RouteEntity route = routeService.getRouteByCities(departureCity, arrivalCity)
                .orElseGet(() -> {
                    RouteEntity newRoute = new RouteEntity();
                    newRoute.setDepartureCity(departureCity);
                    newRoute.setArrivalCity(arrivalCity);
                    newRoute.setBasePrice(price);
                    newRoute.setAverageDuration(calculateDuration(departureTime, arrivalTime));
                    newRoute.setDistance(calculateDistance(departureCity.getName(), arrivalCity.getName()));
                    return routeService.createRoute(newRoute);
                });

        return createFlight(flightNumber, route, departureTime, arrivalTime, price, aircraft, airline);
    }

    // Метод для ручной инвалидации кеша
    @CacheEvict(value = {"flights", "seats"}, allEntries = true)
    public void evictAllCaches() {
        log.info("Evicting all flight and seat caches");
    }

    // Инвалидация кеша конкретного рейса
    @Caching(evict = {
            @CacheEvict(value = "flights", key = "'detail-' + #flightId"),
            @CacheEvict(value = "flights", key = "'simple-' + #flightId"),
            @CacheEvict(value = "seats", key = "'available-' + #flightId"),
            @CacheEvict(value = "seats", key = "'count-' + #flightId")
    })
    public void evictFlightCache(Long flightId) {
        log.info("Evicting cache for flight: {}", flightId);
    }

    private Integer calculateDuration(LocalDateTime departure, LocalDateTime arrival) {
        return (int) java.time.Duration.between(departure, arrival).toMinutes();
    }

    private Integer calculateDistance(String from, String to) {
        return switch (from + "-" + to) {
            case "New York-London" -> 5567;
            case "New York-Paris" -> 5834;
            case "London-Tokyo" -> 9560;
            case "Paris-Dubai" -> 5167;
            case "Tokyo-Sydney" -> 7821;
            case "Dubai-Singapore" -> 5846;
            default -> 1000;
        };
    }
}