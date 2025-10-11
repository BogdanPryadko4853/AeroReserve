package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.service.core.BookingService;
import com.bogdan.aeroreserve.service.notification.PdfTicketService;
import com.bogdan.aeroreserve.service.core.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class TicketController {

    private final PdfTicketService pdfTicketService;
    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/booking/{id}/ticket")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BookingEntity booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Проверяем, что бронирование принадлежит пользователю
            if (!booking.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied");
            }

            // Проверяем, что бронирование оплачено
            if (!booking.isPaid()) {
                throw new RuntimeException("Ticket is not available for unpaid bookings");
            }

            // Генерируем PDF
            byte[] pdfBytes = pdfTicketService.generateTicketPdf(booking);

            // Создаем ответ для скачивания
            String filename = "ticket-" + booking.getBookingNumber() + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(pdfBytes.length))
                    .body(pdfBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate ticket: " + e.getMessage(), e);
        }
    }
}