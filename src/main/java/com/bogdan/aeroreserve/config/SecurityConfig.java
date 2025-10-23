package com.bogdan.aeroreserve.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности приложения Spring Security.
 * Определяет правила доступа, аутентификации и авторизации для различных эндпоинтов.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Настраивает цепочку фильтров безопасности для HTTP запросов.
     * Определяет правила авторизации, настройки CSRF, форму входа и выхода.
     *
     * @param http объект для настройки веб-безопасности
     * @return сконфигурированная цепочка фильтров безопасности
     * @throws Exception если произошла ошибка при конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        // Отключаем CSRF защиту для API резервного копирования
                        .ignoringRequestMatchers("/api/admin/backup/**")
                )
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ к статическим ресурсам без аутентификации
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // Разрешаем доступ к общедоступным страницам
                        .requestMatchers("/", "/css/**", "/js/**", "/register", "/login").permitAll()
                        // Требуем роль ADMIN для доступа к админским эндпоинтам
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // Настраиваем кастомную страницу входа
                        .loginPage("/login")
                        // URL перенаправления после успешного входа
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        // URL перенаправления после выхода
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Создает кодировщик паролей для безопасного хранения паролей пользователей.
     * Использует алгоритм BCrypt с силой хеширования по умолчанию.
     *
     * @return кодировщик паролей BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}