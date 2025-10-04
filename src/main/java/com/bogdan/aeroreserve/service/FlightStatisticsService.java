package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.FlightStatisticsEntity;
import com.bogdan.aeroreserve.enums.BookingStatus;
import com.bogdan.aeroreserve.repository.FlightStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightStatisticsService {
    private final FlightStatisticsRepository statisticsRepository;
    private final FlightService flightService;
    private final BookingService bookingService;

    public FlightStatisticsEntity getOrCreateStatistics(Long flightId) {
        return statisticsRepository.findByFlightId(flightId)
                .orElseGet(() -> {
                    FlightEntity flight = flightService.getFlightById(flightId)
                            .orElseThrow(() -> new RuntimeException("Flight not found"));
                    return statisticsRepository.save(new FlightStatisticsEntity(flight));
                });
    }

    public FlightStatisticsEntity updateStatistics(Long flightId) {
        FlightStatisticsEntity stats = getOrCreateStatistics(flightId);
        FlightEntity flight = flightService.getFlightById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        // Получаем все брони для этого рейса
        List<BookingEntity> bookings = bookingService.getBookingsByFlight(flight);

        // Обновляем статистику
        stats.setTotalBookings(bookings.size());
        stats.setTotalPassengers((int) bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count());

        // Рассчитываем заполненность
        stats.calculateLoadFactor();

        // Обновляем время
        stats.setLastUpdated(java.time.LocalDateTime.now());

        return statisticsRepository.save(stats);
    }

    public List<FlightStatisticsEntity> getAllStatistics() {
        return statisticsRepository.findAllByOrderByLastUpdatedDesc();
    }

    public List<FlightStatisticsEntity> getTopPerformingFlights() {
        return statisticsRepository.findByOnTimePerformanceGreaterThanEqual(90.0);
    }

    public void initializeStatisticsForAllFlights() {
        List<FlightEntity> flights = flightService.getAllFlights();
        for (FlightEntity flight : flights) {
            if (statisticsRepository.findByFlightId(flight.getId()).isEmpty()) {
                FlightStatisticsEntity stats = new FlightStatisticsEntity(flight);

                // Генерируем случайные данные для демонстрации
                stats.setTotalBookings((int) (Math.random() * flight.getAircraft().getTotalSeats()));
                stats.setTotalPassengers((int) (stats.getTotalBookings() * 0.8));
                stats.setAverageDelayMinutes(Math.random() * 30);
                stats.setOnTimePerformance(85 + Math.random() * 15);
                stats.setCustomerSatisfactionScore(7 + Math.random() * 3);
                stats.calculateLoadFactor();

                statisticsRepository.save(stats);
            }
        }
    }
}
