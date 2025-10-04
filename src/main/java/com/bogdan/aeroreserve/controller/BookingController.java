package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.service.BookingService;
import com.bogdan.aeroreserve.service.FlightService;
import com.bogdan.aeroreserve.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final FlightService flightService;
    private final UserService userService;

    @GetMapping("/book/{flightId}")
    public String bookForm(@PathVariable Long flightId,
                           @RequestParam String seatNumber,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        model.addAttribute("flight", flightService.getFlightById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found")));
        model.addAttribute("seatNumber", seatNumber);

        UserEntity user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);

        return "booking-form";
    }

    @PostMapping("/book/{flightId}")
    public String bookFlight(
            @PathVariable Long flightId,
            @RequestParam String seatNumber,
            @RequestParam String passengerName,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        try {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            var booking = bookingService.createBooking(user, flightId, seatNumber, passengerName);
            return "redirect:/dashboard?booked=" + booking.getBookingNumber();

        } catch (Exception e) {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("flight", flightService.getFlightById(flightId).orElse(null));
            model.addAttribute("availableSeats", flightService.getAvailableSeats(flightId));
            model.addAttribute("seatNumber", seatNumber);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("flight", flightService.getFlightById(flightId).orElse(null));
            model.addAttribute("availableSeats", flightService.getAvailableSeats(flightId));
            return "booking-form";
        }
    }

    @PostMapping("/booking/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return "redirect:/dashboard?cancelled=true";
    }
}