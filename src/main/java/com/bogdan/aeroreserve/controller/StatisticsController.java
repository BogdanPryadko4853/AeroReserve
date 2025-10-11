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

@Controller
@RequiredArgsConstructor
public class StatisticsController {
    private final FlightStatisticsService statisticsService;
    private final UserService userService;

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

    @GetMapping("/statistics/flight/{flightId}")
    public String flightStatistics(@PathVariable Long flightId, Model model,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("stats", statisticsService.getOrCreateStatistics(flightId));
        model.addAttribute("user", user);
        return "flight-statistics";
    }

    @GetMapping("/statistics/refresh")
    public String refreshStatistics() {
        statisticsService.initializeStatisticsForAllFlights();
        return "redirect:/statistics";
    }
}