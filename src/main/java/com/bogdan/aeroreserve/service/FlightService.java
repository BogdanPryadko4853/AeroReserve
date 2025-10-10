package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.*;
import com.bogdan.aeroreserve.enums.FlightStatus;
import com.bogdan.aeroreserve.repository.FlightRepository;
import com.bogdan.aeroreserve.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final RouteService routeService;
    private final CityService cityService;

    public List<FlightEntity> searchFlights(String from, String to, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        // Ищем города по названию
        CityEntity departureCity = cityService.getCityByName(from)
                .orElseThrow(() -> new RuntimeException("Departure city not found: " + from));
        CityEntity arrivalCity = cityService.getCityByName(to)
                .orElseThrow(() -> new RuntimeException("Arrival city not found: " + to));

        // Ищем маршрут между городами
        RouteEntity route = routeService.getRouteByCities(departureCity, arrivalCity)
                .orElseThrow(() -> new RuntimeException("Route not found between " + from + " and " + to));

        return flightRepository.findByRouteAndDepartureTimeBetween(route, start, end);
    }

    public List<FlightEntity> searchFlightsByCityNames(String from, String to, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return flightRepository.findByRoute_DepartureCity_NameAndRoute_ArrivalCity_NameAndDepartureTimeBetween(
                from, to, start, end);
    }

    public Page<FlightEntity> getAllFlights(Pageable pageable) {
        return flightRepository.findAll(pageable);
    }

    public Page<FlightEntity> searchFlights(String from, String to, LocalDate date, Pageable pageable) {
        // Если параметры поиска пустые, возвращаем все рейсы
        if ((from == null || from.trim().isEmpty()) &&
                (to == null || to.trim().isEmpty()) &&
                date == null) {
            return flightRepository.findAll(pageable);
        }

        // Иначе ищем по критериям
        return flightRepository.findBySearchCriteria(from, to, date, pageable);
    }

    public Optional<FlightEntity> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public List<SeatEntity> getAvailableSeats(Long flightId) {
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return seatRepository.findByFlightAndAvailableTrue(flight);
    }

    public FlightEntity createFlight(String flightNumber, RouteEntity route,
                                     LocalDateTime departureTime, LocalDateTime arrivalTime,
                                     BigDecimal price, AircraftEntity aircraft, AirlineEntity airline) {
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

    public FlightEntity createFlightWithCities(String flightNumber, String departureCityName,
                                               String arrivalCityName, LocalDateTime departureTime,
                                               LocalDateTime arrivalTime, BigDecimal price,
                                               AircraftEntity aircraft, AirlineEntity airline) {
        // Находим города
        CityEntity departureCity = cityService.getCityByName(departureCityName)
                .orElseThrow(() -> new RuntimeException("Departure city not found: " + departureCityName));
        CityEntity arrivalCity = cityService.getCityByName(arrivalCityName)
                .orElseThrow(() -> new RuntimeException("Arrival city not found: " + arrivalCityName));

        // Находим или создаем маршрут
        RouteEntity route = routeService.getRouteByCities(departureCity, arrivalCity)
                .orElseGet(() -> {
                    RouteEntity newRoute = new RouteEntity();
                    newRoute.setDepartureCity(departureCity);
                    newRoute.setArrivalCity(arrivalCity);
                    newRoute.setBasePrice(price);
                    // Рассчитываем примерную длительность и расстояние
                    newRoute.setAverageDuration(calculateDuration(departureTime, arrivalTime));
                    newRoute.setDistance(calculateDistance(departureCity.getName(), arrivalCity.getName()));
                    return routeService.createRoute(newRoute);
                });

        return createFlight(flightNumber, route, departureTime, arrivalTime, price, aircraft, airline);
    }

    private Integer calculateDuration(LocalDateTime departure, LocalDateTime arrival) {
        return (int) java.time.Duration.between(departure, arrival).toMinutes();
    }

    private Integer calculateDistance(String from, String to) {
        // Простая заглушка для расчета расстояния
        // В реальном приложении можно использовать API для расчета расстояний
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