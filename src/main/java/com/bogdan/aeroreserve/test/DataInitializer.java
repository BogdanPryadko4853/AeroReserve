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
import java.util.Random;

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
    private final Random random = new Random();

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
        countryService.createCountry("Canada", "CA", "CAD", "America/Toronto");
        countryService.createCountry("China", "CN", "CNY", "Asia/Shanghai");
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
        CountryEntity ca = countries.stream().filter(c -> c.getCode().equals("CA")).findFirst().get();
        CountryEntity cn = countries.stream().filter(c -> c.getCode().equals("CN")).findFirst().get();

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
        cityService.createCity("Toronto", "YYZ", ca);
        cityService.createCity("Beijing", "PEK", cn);
        cityService.createCity("Shanghai", "PVG", cn);
        cityService.createCity("Frankfurt", "FRA", de);
        cityService.createCity("Amsterdam", "AMS", countries.stream().filter(c -> c.getCode().equals("NL")).findFirst().orElse(de));
        cityService.createCity("Istanbul", "IST", countries.stream().filter(c -> c.getCode().equals("TR")).findFirst().orElse(ru));
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
        airlineService.createAirline("Qatar Airways", "QR", "Qatar");
        airlineService.createAirline("Turkish Airlines", "TK", "Turkey");
        airlineService.createAirline("Qantas", "QF", "Australia");
        airlineService.createAirline("Air Canada", "AC", "Canada");
        airlineService.createAirline("Japan Airlines", "JL", "Japan");
        airlineService.createAirline("Korean Air", "KE", "South Korea");
    }

    private void createSampleAircrafts() {
        aircraftService.createAircraft("Boeing 737-800", "Boeing", 162, 0, 0);
        aircraftService.createAircraft("Boeing 777-300", "Boeing", 220, 48, 8);
        aircraftService.createAircraft("Boeing 787 Dreamliner", "Boeing", 180, 32, 12);
        aircraftService.createAircraft("Airbus A320", "Airbus", 150, 0, 0);
        aircraftService.createAircraft("Airbus A330", "Airbus", 200, 36, 12);
        aircraftService.createAircraft("Airbus A380", "Airbus", 400, 80, 20);
        aircraftService.createAircraft("Boeing 747-8", "Boeing", 350, 60, 15);
        aircraftService.createAircraft("Airbus A350", "Airbus", 280, 40, 18);
        aircraftService.createAircraft("Embraer E195", "Embraer", 120, 0, 0);
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
        CityEntity losAngeles = cities.stream().filter(c -> c.getIataCode().equals("LAX")).findFirst().get();
        CityEntity chicago = cities.stream().filter(c -> c.getIataCode().equals("ORD")).findFirst().get();
        CityEntity toronto = cities.stream().filter(c -> c.getIataCode().equals("YYZ")).findFirst().get();
        CityEntity beijing = cities.stream().filter(c -> c.getIataCode().equals("PEK")).findFirst().get();
        CityEntity frankfurt = cities.stream().filter(c -> c.getIataCode().equals("FRA")).findFirst().get();
        CityEntity istanbul = cities.stream().filter(c -> c.getIataCode().equals("IST")).findFirst().get();

        // Создаем больше маршрутов для разнообразия
        List<RouteEntity> routes = Arrays.asList(
                createRoute(newYork, london, new BigDecimal("499.99"), 420, 5567),
                createRoute(newYork, paris, new BigDecimal("459.99"), 435, 5834),
                createRoute(london, tokyo, new BigDecimal("899.99"), 720, 9560),
                createRoute(paris, dubai, new BigDecimal("699.99"), 390, 5167),
                createRoute(tokyo, sydney, new BigDecimal("799.99"), 585, 7821),
                createRoute(dubai, singapore, new BigDecimal("549.99"), 465, 5846),
                createRoute(moscow, dubai, new BigDecimal("399.99"), 300, 3724),
                createRoute(losAngeles, tokyo, new BigDecimal("849.99"), 600, 8800),
                createRoute(chicago, london, new BigDecimal("529.99"), 450, 6400),
                createRoute(toronto, frankfurt, new BigDecimal("629.99"), 480, 6200),
                createRoute(beijing, singapore, new BigDecimal("429.99"), 360, 4400),
                createRoute(frankfurt, istanbul, new BigDecimal("329.99"), 180, 2200),
                createRoute(sydney, losAngeles, new BigDecimal("1199.99"), 840, 12000),
                createRoute(istanbul, dubai, new BigDecimal("349.99"), 240, 3100),
                createRoute(paris, frankfurt, new BigDecimal("199.99"), 90, 450)
        );

        routeRepository.saveAll(routes);
    }

    private RouteEntity createRoute(CityEntity departure, CityEntity arrival, BigDecimal price, int duration, int distance) {
        RouteEntity route = new RouteEntity();
        route.setDepartureCity(departure);
        route.setArrivalCity(arrival);
        route.setBasePrice(price);
        route.setAverageDuration(duration);
        route.setDistance(distance);
        return route;
    }

    private void createSampleFlights() {
        List<AircraftEntity> aircrafts = aircraftService.getAllAircrafts();
        List<AirlineEntity> airlines = airlineService.getAllAirlines();
        List<RouteEntity> routes = routeRepository.findAll();

        List<FlightEntity> flights = new ArrayList<>();

        // Создаем 20 рейсов с разными авиакомпаниями
        for (int i = 1; i <= 20; i++) {
            AirlineEntity airline = getRandomAirline(airlines);
            RouteEntity route = routes.get(random.nextInt(routes.size()));
            AircraftEntity aircraft = aircrafts.get(random.nextInt(aircrafts.size()));

            String flightNumber = generateFlightNumber(airline, i);

            LocalDateTime departureTime = generateRandomDepartureTime(i);
            LocalDateTime arrivalTime = departureTime.plusMinutes(route.getAverageDuration());

            FlightEntity flight = createFlight(flightNumber, route, aircraft, airline, departureTime, arrivalTime);
            flights.add(flight);
        }

        for (FlightEntity flight : flights) {
            FlightEntity savedFlight = flightRepository.save(flight);
            createSeatsForFlight(savedFlight);
        }
    }

    private AirlineEntity getRandomAirline(List<AirlineEntity> airlines) {
        return airlines.get(random.nextInt(airlines.size()));
    }

    private String generateFlightNumber(AirlineEntity airline, int index) {
        String airlineCode = airline.getCode();
        int flightNum = 100 + (index * 10) + random.nextInt(50);
        return airlineCode + flightNum;
    }

    private LocalDateTime generateRandomDepartureTime(int dayOffset) {
        int hour = 6 + random.nextInt(14); // между 6:00 и 20:00
        int minute = random.nextInt(4) * 15; // 0, 15, 30, 45 минут
        return LocalDateTime.now().plusDays(dayOffset).withHour(hour).withMinute(minute);
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

        // Добавляем случайное отклонение к базовой цене (±20%)
        BigDecimal basePrice = route.getBasePrice();
        double variation = 0.8 + (random.nextDouble() * 0.4); // от 0.8 до 1.2
        BigDecimal finalPrice = basePrice.multiply(BigDecimal.valueOf(variation));
        flight.setPrice(finalPrice);

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