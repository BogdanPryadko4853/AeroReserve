package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.TicketEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final TicketService ticketService;
    private final PdfTicketService pdfTicketService;

    /**
     * Отправка email с билетом после успешной оплаты
     */
    public void sendBookingConfirmation(BookingEntity booking, TicketEntity ticket) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Настройка email
            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("🎫 Your Flight Ticket - Booking #" + booking.getBookingNumber());
            helper.setFrom("no-reply@aeroreserve.com", "AeroReserve");

            Map<String, Object> variables = new HashMap<>();
            variables.put("booking", booking);
            variables.put("user", booking.getUser());
            variables.put("flight", booking.getFlight());
            variables.put("seat", booking.getSeat());
            variables.put("ticket", ticket);

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process("booking-confirmation", context);

            helper.setText(htmlContent, true);

            // Прикрепляем PDF билета
            byte[] pdfBytes = pdfTicketService.generateTicketPdfWithTicketInfo(booking, ticket);
            helper.addAttachment("ticket-" + ticket.getTicketNumber() + ".pdf",
                    new ByteArrayResource(pdfBytes));

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send confirmation email: " + e.getMessage(), e);
        }
    }

    /**
     * Отправка уведомления о возврате средств
     */
    public void sendRefundNotification(BookingEntity booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("💰 Refund Processed - Booking #" + booking.getBookingNumber());
            helper.setFrom("no-reply@aeroreserve.com", "AeroReserve");

            Map<String, Object> variables = new HashMap<>();
            variables.put("booking", booking);
            variables.put("user", booking.getUser());
            variables.put("flight", booking.getFlight());

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process("refund-notification", context);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send refund notification: " + e.getMessage(), e);
        }
    }

    /**
     * Отправка уведомления об отмене бронирования
     */
    public void sendCancellationNotification(BookingEntity booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("❌ Booking Cancelled - #" + booking.getBookingNumber());
            helper.setFrom("no-reply@aeroreserve.com", "AeroReserve");

            Map<String, Object> variables = new HashMap<>();
            variables.put("booking", booking);
            variables.put("user", booking.getUser());
            variables.put("flight", booking.getFlight());

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process("cancellation-notification", context);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send cancellation notification: " + e.getMessage(), e);
        }
    }
}