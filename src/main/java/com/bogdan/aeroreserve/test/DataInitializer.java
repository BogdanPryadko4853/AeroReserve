package com.bogdan.aeroreserve.test;

import com.bogdan.aeroreserve.entity.AircraftEntity;
import com.bogdan.aeroreserve.entity.AirlineEntity;
import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.SeatEntity;
import com.bogdan.aeroreserve.enums.SeatClass;
import com.bogdan.aeroreserve.repository.FlightRepository;
import com.bogdan.aeroreserve.repository.SeatRepository;
import com.bogdan.aeroreserve.service.AircraftService;
import com.bogdan.aeroreserve.service.AirlineService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final AircraftService aircraftService;
    private final AirlineService airlineService;

    @PostConstruct
    public void init() {
        if (aircraftService.getAllAircrafts().isEmpty()) {
            createSampleAirlines();
            createSampleAircrafts();
            createSampleFlights();
        }
    }

    private void createSampleAirlines() {
        airlineService.createAirline("AeroReserve", "AR", "International");
        airlineService.createAirline("Aeroflot", "SU", "Russia");
        airlineService.createAirline("Lufthansa", "LH", "Germany");
        airlineService.createAirline("Emirates", "EK", "UAE");
        airlineService.createAirline("British Airways", "BA", "United Kingdom");
        airlineService.createAirline("Air France", "AF", "France");
    }

    private void createSampleAircrafts() {
        aircraftService.createAircraft("Boeing 737-800", "Boeing", 162, 0, 0);
        aircraftService.createAircraft("Boeing 777-300", "Boeing", 220, 48, 8);
        aircraftService.createAircraft("Boeing 787 Dreamliner", "Boeing", 180, 32, 12);
        aircraftService.createAircraft("Airbus A320", "Airbus", 150, 0, 0);
        aircraftService.createAircraft("Airbus A330", "Airbus", 200, 36, 12);
        aircraftService.createAircraft("Airbus A380", "Airbus", 400, 80, 20);
    }

    private void createSampleFlights() {
        List<AircraftEntity> aircrafts = aircraftService.getAllAircrafts();
        List<AirlineEntity> airlines = airlineService.getAllAirlines();

        AirlineEntity mainAirline = airlines.get(0);

        List<FlightEntity> flights = Arrays.asList(
                new FlightEntity("AR101", "New York", "London",
                        LocalDateTime.now().plusDays(1).withHour(8).withMinute(0),
                        LocalDateTime.now().plusDays(1).withHour(20).withMinute(0),
                        new BigDecimal("499.99"), aircrafts.get(1), mainAirline),
                new FlightEntity("AR102", "New York", "Paris",
                        LocalDateTime.now().plusDays(2).withHour(14).withMinute(30),
                        LocalDateTime.now().plusDays(2).withHour(23).withMinute(45),
                        new BigDecimal("459.99"), aircrafts.get(4), mainAirline),
                new FlightEntity("AR201", "London", "Tokyo",
                        LocalDateTime.now().plusDays(3).withHour(10).withMinute(0),
                        LocalDateTime.now().plusDays(3).withHour(18).withMinute(0),
                        new BigDecimal("899.99"), aircrafts.get(2), mainAirline),
                new FlightEntity("AR202", "Paris", "Dubai",
                        LocalDateTime.now().plusDays(1).withHour(16).withMinute(15),
                        LocalDateTime.now().plusDays(1).withHour(23).withMinute(30),
                        new BigDecimal("699.99"), aircrafts.get(5), mainAirline),
                new FlightEntity("AR301", "Tokyo", "Sydney",
                        LocalDateTime.now().plusDays(4).withHour(9).withMinute(30),
                        LocalDateTime.now().plusDays(4).withHour(22).withMinute(45),
                        new BigDecimal("799.99"), aircrafts.get(3), mainAirline),
                new FlightEntity("AR302", "Dubai", "Singapore",
                        LocalDateTime.now().plusDays(2).withHour(11).withMinute(0),
                        LocalDateTime.now().plusDays(2).withHour(19).withMinute(30),
                        new BigDecimal("549.99"), aircrafts.get(0), mainAirline)
        );

        for (FlightEntity flight : flights) {
            FlightEntity savedFlight = flightRepository.save(flight);
            createSeatsForFlight(savedFlight);
        }


    }

    private void createSeatsForFlight(FlightEntity flight) {
        List<SeatEntity> seats = new ArrayList<>();
        AircraftEntity aircraft = flight.getAircraft();

        // First Class
        for (int i = 1; i <= aircraft.getFirstClassSeats(); i++) {
            seats.add(new SeatEntity(flight, i + "F", SeatClass.FIRST));
        }

        // Business Class
        for (int i = 1; i <= aircraft.getBusinessSeats(); i++) {
            seats.add(new SeatEntity(flight, i + "B", SeatClass.BUSINESS));
        }

        // Economy Class
        for (int i = 1; i <= aircraft.getEconomySeats(); i++) {
            seats.add(new SeatEntity(flight, i + "E", SeatClass.ECONOMY));
        }

        seatRepository.saveAll(seats);
    }
}