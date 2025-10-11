package com.bogdan.aeroreserve.service.core;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.FlightStatisticsEntity;
import com.bogdan.aeroreserve.enums.BookingStatus;
import com.bogdan.aeroreserve.repository.FlightStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Pageable pageable = PageRequest.of(0, 1000);
        Page<FlightEntity> flightsPage = flightService.getAllFlights(pageable);
        List<FlightEntity> flights = flightsPage.getContent();

        for (FlightEntity flight : flights) {
            getOrCreateStatistics(flight.getId());
        }
    }

}
