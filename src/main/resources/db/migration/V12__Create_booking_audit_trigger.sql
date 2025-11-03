-- Таблица для аудита изменений бронирований
CREATE TABLE booking_audit_log (
                                   id BIGSERIAL PRIMARY KEY,
                                   booking_id BIGINT NOT NULL,
                                   old_status VARCHAR(30),
                                   new_status VARCHAR(30) NOT NULL,
                                   changed_by VARCHAR(255) DEFAULT CURRENT_USER,
                                   changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   ip_address INET,
                                   user_agent TEXT,
                                   additional_info JSONB
);

-- Функция триггера для логирования изменений статусов бронирований
CREATE OR REPLACE FUNCTION log_booking_status_changes()
    RETURNS TRIGGER AS $$
BEGIN
    -- Логируем только если изменился статус
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        INSERT INTO booking_audit_log (
            booking_id,
            old_status,
            new_status,
            additional_info
        ) VALUES (
                     NEW.id,
                     OLD.status,
                     NEW.status,
                     jsonb_build_object(
                             'flight_id', NEW.flight_id,
                             'user_id', NEW.user_id,
                             'total_price', NEW.total_price,
                             'passenger_name', NEW.passenger_name
                     )
                 );
    END IF;

    -- Автоматически обновляем статус мест при отмене бронирования
    IF OLD.status != 'CANCELLED' AND NEW.status = 'CANCELLED' THEN
        UPDATE seats
        SET available = true
        WHERE id = NEW.seat_id;

        -- Также отменяем связанные платежи
        UPDATE payments
        SET status = 'CANCELLED',
            updated_at = CURRENT_TIMESTAMP
        WHERE booking_id = NEW.id
          AND status IN ('PENDING', 'PROCESSING');
    END IF;

    -- При подтверждении бронирования резервируем место
    IF OLD.status != 'CONFIRMED' AND NEW.status = 'CONFIRMED' THEN
        UPDATE seats
        SET available = false
        WHERE id = NEW.seat_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер
CREATE TRIGGER trigger_log_booking_changes
    AFTER UPDATE ON bookings
    FOR EACH ROW
EXECUTE FUNCTION log_booking_status_changes();