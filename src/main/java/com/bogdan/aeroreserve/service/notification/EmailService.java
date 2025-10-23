package com.bogdan.aeroreserve.service.notification;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.TicketEntity;
import com.bogdan.aeroreserve.service.core.TicketService;
import com.bogdan.aeroreserve.service.generator.PdfTicketService;
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

/**
 * Сервис для отправки email уведомлений
 * Обрабатывает отправку подтверждений, уведомлений о возвратах и отменах
 *
 * @author Bogdan
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class EmailService implements NotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final TicketService ticketService;
    private final PdfTicketService pdfTicketService;

    /**
     * Отправляет email с подтверждением бронирования и прикрепленным билетом
     *
     * @param booking данные бронирования
     * @param ticket данные билета
     * @throws RuntimeException если не удалось отправить email
     */
    public void sendBookingConfirmation(BookingEntity booking, TicketEntity ticket) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

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

            byte[] pdfBytes = pdfTicketService.generateTicket(booking, ticket);
            helper.addAttachment("ticket-" + ticket.getTicketNumber() + ".pdf",
                    new ByteArrayResource(pdfBytes));

            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send confirmation email: " + e.getMessage(), e);
        }
    }

    /**
     * Отправляет уведомление о возврате средств
     *
     * @param booking данные бронирования
     * @throws RuntimeException если не удалось отправить email
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
     * Отправляет уведомление об отмене бронирования
     *
     * @param booking данные бронирования
     * @throws RuntimeException если не удалось отправить email
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