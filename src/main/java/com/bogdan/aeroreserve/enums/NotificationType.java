package com.bogdan.aeroreserve.enums;

/**
 * Перечисление типов уведомлений
 * Определяет категории системных уведомлений
 *
 * @author Bogdan
 * @version 1.0
 */
public enum NotificationType {
    /**
     * Информационное уведомление
     */
    INFO,

    /**
     * Предупреждающее уведомление
     */
    WARNING,

    /**
     * Уведомление об успешной операции
     */
    SUCCESS,

    /**
     * Уведомление об ошибке
     */
    ERROR
}