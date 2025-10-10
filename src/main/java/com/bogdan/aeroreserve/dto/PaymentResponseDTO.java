package com.bogdan.aeroreserve.dto;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private String clientSecret;
    private String paymentIntentId;
    private String status;
    private String errorMessage;
    private boolean requiresAction;
    private String paymentUrl;

    public PaymentResponseDTO(String clientSecret, String paymentIntentId, String status) {
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
        this.status = status;
    }

    public PaymentResponseDTO() {}
}
