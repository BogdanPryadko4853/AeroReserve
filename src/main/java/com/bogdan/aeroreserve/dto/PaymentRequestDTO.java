package com.bogdan.aeroreserve.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    private Long bookingId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethodId;
    private String returnUrl;
}