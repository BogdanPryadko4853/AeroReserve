package com.bogdan.aeroreserve.controller;

import com.bogdan.aeroreserve.dto.PaymentResponseDTO;
import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.service.core.BookingService;
import com.bogdan.aeroreserve.service.payment.PaymentService;
import com.bogdan.aeroreserve.service.core.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final UserService userService;


    /**
     * Создание платежного намерения (API endpoint)
     */
    @PostMapping("/api/payment/create-intent")
    @ResponseBody
    public ResponseEntity<PaymentResponseDTO> createPaymentIntent(@RequestParam Long bookingId) {
        try {
            BookingEntity booking = bookingService.getBookingById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            var payment = paymentService.createPaymentIntent(booking);

            PaymentResponseDTO response = new PaymentResponseDTO(
                    payment.getClientSecret(),
                    payment.getStripePaymentIntentId(),
                    payment.getStatus()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            PaymentResponseDTO errorResponse = new PaymentResponseDTO();
            errorResponse.setErrorMessage(e.getMessage());
            errorResponse.setStatus("error");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Страница успешной оплаты
     */
    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam(required = false) String payment_intent,
                                 @RequestParam(required = false) String session_id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        try {
            String paymentIntentId = payment_intent != null ? payment_intent :
                    session_id != null ? getPaymentIntentFromSession(session_id) : null;

            if (paymentIntentId != null) {
                // Подтверждаем платеж
                var payment = paymentService.confirmPayment(paymentIntentId);

                // Подтверждаем бронирование
                bookingService.confirmBooking(payment.getBooking().getId());

                model.addAttribute("booking", payment.getBooking());
                model.addAttribute("payment", payment);
            }

            UserEntity user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("user", user);

            return "payment-success";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "payment-error";
        }
    }

    /**
     * Страница отмены оплаты
     */
    @GetMapping("/payment/cancel")
    public String paymentCancel(@RequestParam(required = false) Long bookingId,
                                Model model) {
        if (bookingId != null) {
            // Отменяем бронирование при отмене оплаты
            bookingService.cancelBooking(bookingId);
            model.addAttribute("bookingId", bookingId);
        }

        return "payment-cancel";
    }

    /**
     * Вебхук для обработки событий от Stripe
     */
    @PostMapping("/webhook/stripe")
    @ResponseBody
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(event);
                    break;
                default:
                    System.out.println("Unhandled event type: " + event.getType());
            }

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Webhook error: " + e.getMessage());
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        String paymentIntentId = paymentIntent.getId();

        paymentService.confirmPayment(paymentIntentId);

        Optional<BookingEntity> bookingOpt = bookingService.findByPaymentIntentId(paymentIntentId);
        bookingOpt.ifPresent(booking -> {
            bookingService.confirmBooking(booking.getId());
        });
    }

    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
        String paymentIntentId = paymentIntent.getId();

        // Отменяем бронирование при неудачной оплате
        Optional<BookingEntity> bookingOpt = bookingService.findByPaymentIntentId(paymentIntentId);
        bookingOpt.ifPresent(booking -> {
            bookingService.cancelBooking(booking.getId());
        });
    }

    private void handleCheckoutSessionCompleted(Event event) {
        System.out.println("Checkout session completed: " + event.getId());
    }

    private String getPaymentIntentFromSession(String sessionId) {
        try {
            com.stripe.model.checkout.Session session = com.stripe.model.checkout.Session.retrieve(sessionId);
            return session.getPaymentIntent();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to get payment intent from session", e);
        }
    }
}