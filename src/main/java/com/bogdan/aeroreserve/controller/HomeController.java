package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.service.FlightService;
import com.bogdan.aeroreserve.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import com.bogdan.aeroreserve.service.FlightService;
import lombok.RequiredArgsConstructor;

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
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        
        if (userDetails != null) {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
        }


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("flights", flightService.getAllFlights());
        return "home";
    }

    @GetMapping("/search")
    public String searchFlights(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String date,

            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        if (userDetails != null) {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
        }

            Model model) {

        LocalDate searchDate = LocalDate.parse(date);
        model.addAttribute("flights", flightService.searchFlights(from, to, searchDate));
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("date", date);

        return "home";
    }

    @GetMapping("/flight/{id}")
    public String flightDetails(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        // Добавляем пользователя, если он авторизован
        if (userDetails != null) {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);
        }

    public String flightDetails(@PathVariable Long id, Model model) {
        model.addAttribute("flight", flightService.getFlightById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found")));
        model.addAttribute("availableSeats", flightService.getAvailableSeats(id));
        return "flight-details";
    }
}
}
