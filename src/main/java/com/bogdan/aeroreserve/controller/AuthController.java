package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.service.core.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Контроллер для обработки аутентификации и регистрации пользователей
 *
 * @author Bogdan
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    /**
     * Отображает страницу входа в систему
     *
     * @return имя шаблона страницы входа
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Отображает форму регистрации нового пользователя
     *
     * @param model модель для передачи данных в представление
     * @return имя шаблона страницы регистрации
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "register";
    }

    /**
     * Обрабатывает регистрацию нового пользователя
     *
     * @param user объект пользователя с данными из формы
     * @param model модель для передачи данных в представление
     * @return перенаправление на страницу входа при успехе или возврат к форме при ошибке
     */
    @PostMapping("/register")
    public String register(@ModelAttribute UserEntity user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}