package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.service.FlightService;
import com.bogdan.aeroreserve.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final FlightService flightService;
    private final UserService userService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        if (userDetails != null) {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
        }

        // Создаем pageable с сортировкой по времени вылета
        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime").ascending());
        Page<FlightEntity> flightsPage = flightService.getAllFlights(pageable);

        model.addAttribute("flights", flightsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", flightsPage.getTotalPages());
        model.addAttribute("totalItems", flightsPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "home";
    }

    @GetMapping("/search")
    public String searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        if (userDetails != null) {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
        }

        LocalDate searchDate = LocalDate.parse(date);

        // Создаем pageable с сортировкой по времени вылета
        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime").ascending());
        Page<FlightEntity> flightsPage = flightService.searchFlights(from, to, searchDate, pageable);

        model.addAttribute("flights", flightsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", flightsPage.getTotalPages());
        model.addAttribute("totalItems", flightsPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("date", date);

        return "home";
    }

    @GetMapping("/flight/{id}")
    public String flightDetails(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        if (userDetails != null) {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
        }

        model.addAttribute("flight", flightService.getFlightById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found")));
        model.addAttribute("availableSeats", flightService.getAvailableSeats(id));
        return "flight/flight-details";
    }
}