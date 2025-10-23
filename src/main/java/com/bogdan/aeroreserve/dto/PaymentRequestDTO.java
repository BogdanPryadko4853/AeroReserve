package com.bogdan.aeroreserve.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) для запроса на оплату.
 * Содержит все необходимые данные для инициализации платежной транзакции.
 */
@Data
public class PaymentRequestDTO {

    /**
     * Идентификатор бронирования, для которого выполняется оплата
     */
    private Long bookingId;

    /**
     * Сумма оплаты
     */
    private BigDecimal amount;

    /**
     * Валюта оплаты (например, "USD", "EUR")
     */
    private String currency;

    /**
     * Идентификатор метода оплаты в платежной системе
     */
    private String paymentMethodId;

    /**
     * URL для возврата после завершения оплаты (для redirect-based платежей)
     */
    private String returnUrl;
}