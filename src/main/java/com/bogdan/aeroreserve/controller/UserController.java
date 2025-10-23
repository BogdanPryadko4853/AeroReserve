package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.service.core.BookingService;
import com.bogdan.aeroreserve.service.core.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для пользовательской панели управления
 * Отображает личный кабинет пользователя с его бронированиями
 *
 * @author Bogdan
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor
public class UserController {
    private final BookingService bookingService;
    private final UserService userService;

    /**
     * Отображает панель управления пользователя
     *
     * @param userDetails данные аутентифицированного пользователя
     * @param model модель для передачи данных в представление
     * @return имя шаблона панели управления
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        UserEntity user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("bookings", bookingService.getUserBookings(user));
        return "dashboard";
    }
}