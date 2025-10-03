package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.AircraftEntity;
import com.bogdan.aeroreserve.entity.AirlineEntity;
import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.SeatEntity;
import com.bogdan.aeroreserve.repository.FlightRepository;
import com.bogdan.aeroreserve.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
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

    public List<FlightEntity> searchFlights(String from, String to, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return flightRepository.findByDepartureCityAndArrivalCityAndDepartureTimeBetween(
                from, to, start, end);
    }

    public List<FlightEntity> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<FlightEntity> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public List<SeatEntity> getAvailableSeats(Long flightId) {
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return seatRepository.findByFlightAndAvailableTrue(flight);
    }
    public FlightEntity createFlight(String flightNumber, String departureCity, String arrivalCity,
                                     LocalDateTime departureTime, LocalDateTime arrivalTime,
                                     BigDecimal price, AircraftEntity aircraft, AirlineEntity airline) {
        FlightEntity flight = new FlightEntity(flightNumber, departureCity, arrivalCity,
                departureTime, arrivalTime, price, aircraft, airline);
        return flightRepository.save(flight);
    }
}