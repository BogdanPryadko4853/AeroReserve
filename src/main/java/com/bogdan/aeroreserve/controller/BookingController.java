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

/**
 * Контроллер для управления бронированиями
 * Обрабатывает создание, отмену и возврат бронирований
 *
 * @author Bogdan
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final FlightService flightService;
    private final UserService userService;

    /**
     * Отображает форму бронирования места на рейсе
     *
     * @param flightId идентификатор рейса
     * @param seatNumber номер места
     * @param userDetails данные аутентифицированного пользователя
     * @param model модель для передачи данных в представление
     * @return имя шаблона формы бронирования
     */
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

    /**
     * Обрабатывает создание нового бронирования
     *
     * @param flightId идентификатор рейса
     * @param seatNumber номер места
     * @param passengerName имя пассажира
     * @param userDetails данные аутентифицированного пользователя
     * @param model модель для передачи данных в представление
     * @return перенаправление на страницу оплаты при успехе или возврат к форме при ошибке
     */
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

    /**
     * Отображает страницу оплаты бронирования
     *
     * @param bookingId идентификатор бронирования
     * @param userDetails данные аутентифицированного пользователя
     * @param model модель для передачи данных в представление
     * @return имя шаблона страницы оплаты или перенаправление если оплата уже выполнена
     */
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

    /**
     * Отменяет бронирование
     *
     * @param id идентификатор бронирования
     * @return перенаправление на панель управления с параметром отмены
     */
    @PostMapping("/booking/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return "redirect:/dashboard?cancelled=true";
    }

    /**
     * Обрабатывает возврат средств за бронирование
     *
     * @param id идентификатор бронирования
     * @param userDetails данные аутентифицированного пользователя
     * @param model модель для передачи данных в представление
     * @return перенаправление на панель управления с результатом операции
     */
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

    /**
     * Отображает страницу подтверждения возврата средств
     *
     * @param id идентификатор бронирования
     * @param userDetails данные аутентифицированного пользователя
     * @param model модель для передачи данных в представление
     * @return имя шаблона страницы подтверждения возврата
     */
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

    /**
     * Отменяет платеж и связанное с ним бронирование
     *
     * @param id идентификатор бронирования
     * @param userDetails данные аутентифицированного пользователя
     * @return перенаправление на панель управления с результатом операции
     */
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
            bookingService.cancelBooking(id);

            return "redirect:/dashboard?paymentCancelled=true";

        } catch (Exception e) {
            return "redirect:/dashboard?paymentCancelError=" + e.getMessage();
        }
    }
}