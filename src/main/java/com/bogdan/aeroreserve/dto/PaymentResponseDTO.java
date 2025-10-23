package com.bogdan.aeroreserve.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) для ответа на запрос оплаты.
 * Содержит информацию о результате инициализации платежа.
 */
@Data
public class PaymentResponseDTO {

    /**
     * Секретный ключ клиента для подтверждения платежа на фронтенде
     */
    private String clientSecret;

    /**
     * Идентификатор платежного намерения в платежной системе
     */
    private String paymentIntentId;

    /**
     * Текущий статус платежа
     */
    private String status;

    /**
     * Сообщение об ошибке, если платеж не удался
     */
    private String errorMessage;

    /**
     * Флаг, указывающий требуется ли дополнительное действие от пользователя
     */
    private boolean requiresAction;

    /**
     * URL для перенаправления пользователя для завершения оплаты
     */
    private String paymentUrl;

    /**
     * Конструктор с основными параметрами платежного ответа.
     *
     * @param clientSecret секретный ключ клиента
     * @param paymentIntentId идентификатор платежного намерения
     * @param status статус платежа
     */
    public PaymentResponseDTO(String clientSecret, String paymentIntentId, String status) {
        this.clientSecret = clientSecret;
        this.paymentIntentId = paymentIntentId;
        this.status = status;
    }

    /**
     * Конструктор по умолчанию.
     */
    public PaymentResponseDTO() {}
}