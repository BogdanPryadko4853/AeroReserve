package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.service.core.BookingService;
import com.bogdan.aeroreserve.service.core.FlightService;
import com.bogdan.aeroreserve.service.core.UserService;
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

            return "redirect:/booking/" + booking.getId() + "/payment";

        } catch (Exception e) {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("flight", flightService.getFlightById(flightId).orElse(null));
            model.addAttribute("availableSeats", flightService.getAvailableSeats(flightId));
            model.addAttribute("seatNumber", seatNumber);
            return "booking-form";
        }
    }

    @GetMapping("/booking/{bookingId}/payment")
    public String paymentPage(@PathVariable Long bookingId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        UserEntity user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var booking = bookingService.getBookingById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (booking.isPaid()) {
            return "redirect:/dashboard?alreadyPaid=true";
        }

        model.addAttribute("user", user);
        model.addAttribute("booking", booking);
        model.addAttribute("payment", booking.getPayment());
        model.addAttribute("stripePublicKey", "pk_test_TYooMQauvdEDq54NiTphI7jx");

        return "payment-page";
    }

    @PostMapping("/booking/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return "redirect:/dashboard?cancelled=true";
    }

    @PostMapping("/booking/{id}/refund")
    public String refundBooking(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        try {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BookingEntity booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (!booking.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied");
            }

            bookingService.refundBooking(id);

            return "redirect:/dashboard?refunded=true";

        } catch (Exception e) {
            return "redirect:/dashboard?refundError=" + e.getMessage();
        }
    }

    @GetMapping("/booking/{id}/refund-confirm")
    public String refundConfirmPage(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        UserEntity user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BookingEntity booking = bookingService.getBookingById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        boolean canRefund = bookingService.canRefund(booking);

        model.addAttribute("user", user);
        model.addAttribute("booking", booking);
        model.addAttribute("canRefund", canRefund);

        return "refund-confirm";
    }

    @PostMapping("/booking/{id}/cancel-payment")
    public String cancelPayment(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BookingEntity booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (!booking.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied");
            }
            bookingService.cancelPaymentAndBooking(id);


            return "redirect:/dashboard?paymentCancelled=true";

        } catch (Exception e) {
            return "redirect:/dashboard?paymentCancelError=" + e.getMessage();
        }
    }


}