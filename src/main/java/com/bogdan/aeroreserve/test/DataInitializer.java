package com.bogdan.aeroreserve.test;

import com.bogdan.aeroreserve.entity.*;
import com.bogdan.aeroreserve.enums.SeatClass;
import com.bogdan.aeroreserve.repository.*;
import com.bogdan.aeroreserve.service.*;
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
    private final CountryService countryService;
    private final CityService cityService;
    private final RouteRepository routeRepository;
    private final FlightStatisticsService statisticsService;

    @PostConstruct
    public void init() {
        if (aircraftService.getAllAircrafts().isEmpty()) {
            createSampleCountries();
            createSampleCities();
            createSampleAirlines();
            createSampleAircrafts();
            createSampleRoutes();
            createSampleFlights();
            statisticsService.initializeStatisticsForAllFlights();
        }
    }

    private void createSampleCountries() {
        countryService.createCountry("United States", "US", "USD", "America/New_York");
        countryService.createCountry("United Kingdom", "GB", "GBP", "Europe/London");
        countryService.createCountry("France", "FR", "EUR", "Europe/Paris");
        countryService.createCountry("Germany", "DE", "EUR", "Europe/Berlin");
        countryService.createCountry("United Arab Emirates", "AE", "AED", "Asia/Dubai");
        countryService.createCountry("Japan", "JP", "JPY", "Asia/Tokyo");
        countryService.createCountry("Australia", "AU", "AUD", "Australia/Sydney");
        countryService.createCountry("Singapore", "SG", "SGD", "Asia/Singapore");
        countryService.createCountry("Russia", "RU", "RUB", "Europe/Moscow");
    }

    private void createSampleCities() {
        List<CountryEntity> countries = countryService.getAllCountries();

        CountryEntity us = countries.stream().filter(c -> c.getCode().equals("US")).findFirst().get();
        CountryEntity uk = countries.stream().filter(c -> c.getCode().equals("GB")).findFirst().get();
        CountryEntity fr = countries.stream().filter(c -> c.getCode().equals("FR")).findFirst().get();
        CountryEntity de = countries.stream().filter(c -> c.getCode().equals("DE")).findFirst().get();
        CountryEntity ae = countries.stream().filter(c -> c.getCode().equals("AE")).findFirst().get();
        CountryEntity jp = countries.stream().filter(c -> c.getCode().equals("JP")).findFirst().get();
        CountryEntity au = countries.stream().filter(c -> c.getCode().equals("AU")).findFirst().get();
        CountryEntity sg = countries.stream().filter(c -> c.getCode().equals("SG")).findFirst().get();
        CountryEntity ru = countries.stream().filter(c -> c.getCode().equals("RU")).findFirst().get();

        cityService.createCity("New York", "NYC", us);
        cityService.createCity("London", "LON", uk);
        cityService.createCity("Paris", "PAR", fr);
        cityService.createCity("Berlin", "BER", de);
        cityService.createCity("Dubai", "DXB", ae);
        cityService.createCity("Tokyo", "TYO", jp);
        cityService.createCity("Sydney", "SYD", au);
        cityService.createCity("Singapore", "SIN", sg);
        cityService.createCity("Moscow", "SVO", ru);
        cityService.createCity("Los Angeles", "LAX", us);
        cityService.createCity("Chicago", "ORD", us);
    }

    private void createSampleAirlines() {
        airlineService.createAirline("AeroReserve", "AR", "International");
        airlineService.createAirline("Aeroflot", "SU", "Russia");
        airlineService.createAirline("Lufthansa", "LH", "Germany");
        airlineService.createAirline("Emirates", "EK", "UAE");
        airlineService.createAirline("British Airways", "BA", "United Kingdom");
        airlineService.createAirline("Air France", "AF", "France");
        airlineService.createAirline("Delta Air Lines", "DL", "United States");
        airlineService.createAirline("Singapore Airlines", "SQ", "Singapore");
    }

    private void createSampleAircrafts() {
        aircraftService.createAircraft("Boeing 737-800", "Boeing", 162, 0, 0);
        aircraftService.createAircraft("Boeing 777-300", "Boeing", 220, 48, 8);
        aircraftService.createAircraft("Boeing 787 Dreamliner", "Boeing", 180, 32, 12);
        aircraftService.createAircraft("Airbus A320", "Airbus", 150, 0, 0);
        aircraftService.createAircraft("Airbus A330", "Airbus", 200, 36, 12);
        aircraftService.createAircraft("Airbus A380", "Airbus", 400, 80, 20);
    }

    private void createSampleRoutes() {
        List<CityEntity> cities = cityService.getAllCities();

        CityEntity newYork = cities.stream().filter(c -> c.getIataCode().equals("NYC")).findFirst().get();
        CityEntity london = cities.stream().filter(c -> c.getIataCode().equals("LON")).findFirst().get();
        CityEntity paris = cities.stream().filter(c -> c.getIataCode().equals("PAR")).findFirst().get();
        CityEntity dubai = cities.stream().filter(c -> c.getIataCode().equals("DXB")).findFirst().get();
        CityEntity tokyo = cities.stream().filter(c -> c.getIataCode().equals("TYO")).findFirst().get();
        CityEntity sydney = cities.stream().filter(c -> c.getIataCode().equals("SYD")).findFirst().get();
        CityEntity singapore = cities.stream().filter(c -> c.getIataCode().equals("SIN")).findFirst().get();
        CityEntity moscow = cities.stream().filter(c -> c.getIataCode().equals("SVO")).findFirst().get();

        // Создаем маршруты
        RouteEntity route1 = new RouteEntity();
        route1.setDepartureCity(newYork);
        route1.setArrivalCity(london);
        route1.setBasePrice(new BigDecimal("499.99"));
        route1.setAverageDuration(420); // 7 hours
        route1.setDistance(5567);
        routeRepository.save(route1);

        RouteEntity route2 = new RouteEntity();
        route2.setDepartureCity(newYork);
        route2.setArrivalCity(paris);
        route2.setBasePrice(new BigDecimal("459.99"));
        route2.setAverageDuration(435); // 7 hours 15 min
        route2.setDistance(5834);
        routeRepository.save(route2);

        RouteEntity route3 = new RouteEntity();
        route3.setDepartureCity(london);
        route3.setArrivalCity(tokyo);
        route3.setBasePrice(new BigDecimal("899.99"));
        route3.setAverageDuration(720); // 12 hours
        route3.setDistance(9560);
        routeRepository.save(route3);

        RouteEntity route4 = new RouteEntity();
        route4.setDepartureCity(paris);
        route4.setArrivalCity(dubai);
        route4.setBasePrice(new BigDecimal("699.99"));
        route4.setAverageDuration(390); // 6 hours 30 min
        route4.setDistance(5167);
        routeRepository.save(route4);

        RouteEntity route5 = new RouteEntity();
        route5.setDepartureCity(tokyo);
        route5.setArrivalCity(sydney);
        route5.setBasePrice(new BigDecimal("799.99"));
        route5.setAverageDuration(585); // 9 hours 45 min
        route5.setDistance(7821);
        routeRepository.save(route5);

        RouteEntity route6 = new RouteEntity();
        route6.setDepartureCity(dubai);
        route6.setArrivalCity(singapore);
        route6.setBasePrice(new BigDecimal("549.99"));
        route6.setAverageDuration(465); // 7 hours 45 min
        route6.setDistance(5846);
        routeRepository.save(route6);

        RouteEntity route7 = new RouteEntity();
        route7.setDepartureCity(moscow);
        route7.setArrivalCity(dubai);
        route7.setBasePrice(new BigDecimal("399.99"));
        route7.setAverageDuration(300); // 5 hours
        route7.setDistance(3724);
        routeRepository.save(route7);
    }

    private void createSampleFlights() {
        List<AircraftEntity> aircrafts = aircraftService.getAllAircrafts();
        List<AirlineEntity> airlines = airlineService.getAllAirlines();
        List<RouteEntity> routes = routeRepository.findAll();

        AirlineEntity mainAirline = airlines.stream()
                .filter(a -> a.getCode().equals("AR"))
                .findFirst().get();

        List<FlightEntity> flights = Arrays.asList(
                createFlight("AR101", routes.get(0), aircrafts.get(1), mainAirline,
                        LocalDateTime.now().plusDays(1).withHour(8).withMinute(0),
                        LocalDateTime.now().plusDays(1).withHour(20).withMinute(0)),

                createFlight("AR102", routes.get(1), aircrafts.get(4), mainAirline,
                        LocalDateTime.now().plusDays(2).withHour(14).withMinute(30),
                        LocalDateTime.now().plusDays(2).withHour(23).withMinute(45)),

                createFlight("AR201", routes.get(2), aircrafts.get(2), mainAirline,
                        LocalDateTime.now().plusDays(3).withHour(10).withMinute(0),
                        LocalDateTime.now().plusDays(3).withHour(18).withMinute(0)),

                createFlight("AR202", routes.get(3), aircrafts.get(5), mainAirline,
                        LocalDateTime.now().plusDays(1).withHour(16).withMinute(15),
                        LocalDateTime.now().plusDays(1).withHour(23).withMinute(30)),

                createFlight("AR301", routes.get(4), aircrafts.get(3), mainAirline,
                        LocalDateTime.now().plusDays(4).withHour(9).withMinute(30),
                        LocalDateTime.now().plusDays(4).withHour(22).withMinute(45)),

                createFlight("AR302", routes.get(5), aircrafts.get(0), mainAirline,
                        LocalDateTime.now().plusDays(2).withHour(11).withMinute(0),
                        LocalDateTime.now().plusDays(2).withHour(19).withMinute(30))
        );

        for (FlightEntity flight : flights) {
            FlightEntity savedFlight = flightRepository.save(flight);
            createSeatsForFlight(savedFlight);
        }
    }

    private FlightEntity createFlight(String flightNumber, RouteEntity route, AircraftEntity aircraft,
                                      AirlineEntity airline, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        FlightEntity flight = new FlightEntity();
        flight.setFlightNumber(flightNumber);
        flight.setRoute(route);
        flight.setAircraft(aircraft);
        flight.setAirline(airline);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setPrice(route.getBasePrice()); // Используем базовую цену из маршрута
        flight.setStatus(com.bogdan.aeroreserve.enums.FlightStatus.SCHEDULED);
        return flight;
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