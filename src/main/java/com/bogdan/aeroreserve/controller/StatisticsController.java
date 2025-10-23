package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.service.core.FlightStatisticsService;
import com.bogdan.aeroreserve.service.core.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Контроллер для отображения статистики рейсов
 * Предоставляет аналитику по производительности рейсов
 *
 * @author Bogdan
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor
public class StatisticsController {
    private final FlightStatisticsService statisticsService;
    private final UserService userService;

    /**
     * Отображает главную панель статистики
     *
     * @param model модель для передачи данных в представление
     * @param userDetails данные аутентифицированного пользователя
     * @return имя шаблона панели статистики
     */
    @GetMapping("/statistics")
    public String statisticsDashboard(Model model,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(()-> new RuntimeException("User not found"));
        model.addAttribute("allStatistics", statisticsService.getAllStatistics());
        model.addAttribute("topFlights", statisticsService.getTopPerformingFlights());
        model.addAttribute("user", user);
        return "statistics-dashboard";
    }

    /**
     * Отображает детальную статистику по конкретному рейсу
     *
     * @param flightId идентификатор рейса
     * @param model модель для передачи данных в представление
     * @param userDetails данные аутентифицированного пользователя
     * @return имя шаблона статистики рейса
     */
    @GetMapping("/statistics/flight/{flightId}")
    public String flightStatistics(@PathVariable Long flightId, Model model,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("stats", statisticsService.getOrCreateStatistics(flightId));
        model.addAttribute("user", user);
        return "flight-statistics";
    }

    /**
     * Обновляет статистику для всех рейсов
     *
     * @return перенаправление на панель статистики
     */
    @GetMapping("/statistics/refresh")
    public String refreshStatistics() {
        statisticsService.initializeStatisticsForAllFlights();
        return "redirect:/statistics";
    }
}