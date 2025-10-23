package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.FlightEntity;
import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.enums.BookingStatus;
import com.bogdan.aeroreserve.enums.FlightStatus;
import com.bogdan.aeroreserve.service.core.BookingService;
import com.bogdan.aeroreserve.service.core.FlightService;
import com.bogdan.aeroreserve.service.core.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для административных операций
 * Требует роль ADMIN для доступа ко всем endpoint'ам
 *
 * @author Bogdan
 * @version 1.0
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final FlightService flightService;
    private final BookingService bookingService;

    /**
     * Отображает главную панель администратора
     *
     * @param model модель для передачи данных в представление
     * @return имя шаблона панели администратора
     */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("pageTitle", "Admin Dashboard");
        return "admin/dashboard";
    }

    /**
     * Отображает страницу управления пользователями
     *
     * @param model модель для передачи данных в представление
     * @return имя шаблона управления пользователями
     */
    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<UserEntity> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    /**
     * Удаляет пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return перенаправление на страницу управления пользователями
     */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    /**
     * Отображает страницу управления рейсами с пагинацией
     *
     * @param model модель для передачи данных в представление
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return имя шаблона управления рейсами
     */
    @GetMapping("/flights")
    public String manageFlights(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FlightEntity> flights = flightService.getAllFlights(pageable);

        model.addAttribute("flights", flights);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("flightStatuses", FlightStatus.values());
        return "admin/flights";
    }

    /**
     * Обновляет статус рейса
     *
     * @param id идентификатор рейса
     * @param status новый статус рейса
     * @return перенаправление на страницу управления рейсами
     */
    @PostMapping("/flights/{id}/status")
    public String updateFlightStatus(@PathVariable Long id,
                                     @RequestParam("status") FlightStatus status) {
        flightService.getSimpleFlightById(id).ifPresent(flight -> {
            flight.setStatus(status);
            flightService.updateFlight(flight);
        });
        return "redirect:/admin/flights";
    }

    /**
     * Отображает страницу управления бронированиями
     *
     * @param model модель для передачи данных в представление
     * @return имя шаблона управления бронированиями
     */
    @GetMapping("/bookings")
    public String manageBookings(Model model) {
        List<BookingEntity> bookings = bookingService.getAllBookings();

        model.addAttribute("bookings", bookings);
        model.addAttribute("bookingStatuses", BookingStatus.values());
        return "admin/bookings";
    }

    /**
     * Отменяет бронирование по идентификатору
     *
     * @param id идентификатор бронирования
     * @return перенаправление на страницу управления бронированиями
     */
    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
        } catch (RuntimeException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    /**
     * Выполняет возврат средств за бронирование
     *
     * @param id идентификатор бронирования
     * @return перенаправление на страницу управления бронированиями
     */
    @PostMapping("/bookings/{id}/refund")
    public String refundBooking(@PathVariable Long id) {
        try {
            bookingService.refundBooking(id);
        } catch (RuntimeException e) {
            System.err.println("Error refunding booking: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }
}