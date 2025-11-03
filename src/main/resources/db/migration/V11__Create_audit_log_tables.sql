-- Функция для обновления статистики рейсов при изменении бронирований
CREATE OR REPLACE FUNCTION update_flight_statistics()
    RETURNS TRIGGER AS $$
BEGIN
    -- Обновляем статистику только для подтвержденных бронирований
    IF (TG_OP = 'INSERT' AND NEW.status = 'CONFIRMED') OR
       (TG_OP = 'UPDATE' AND NEW.status = 'CONFIRMED' AND OLD.status != 'CONFIRMED') THEN

        INSERT INTO flight_statistics (
            flight_id,
            total_bookings,
            total_passengers,
            load_factor,
            last_updated
        )
        SELECT
            NEW.flight_id,
            COUNT(b.id),
            COUNT(b.id), -- один пассажир на бронирование
            (COUNT(b.id) * 100.0 / a.total_seats),
            CURRENT_TIMESTAMP
        FROM bookings b
                 JOIN flights f ON f.id = b.flight_id
                 JOIN aircrafts a ON a.id = f.aircraft_id
        WHERE b.flight_id = NEW.flight_id
          AND b.status = 'CONFIRMED'
        GROUP BY b.flight_id, a.total_seats

        ON CONFLICT (flight_id)
            DO UPDATE SET
                          total_bookings = EXCLUDED.total_bookings,
                          total_passengers = EXCLUDED.total_passengers,
                          load_factor = EXCLUDED.load_factor,
                          last_updated = CURRENT_TIMESTAMP;

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер для обновления статистики
CREATE TRIGGER trigger_update_flight_stats
    AFTER INSERT OR UPDATE ON bookings
    FOR EACH ROW
EXECUTE FUNCTION update_flight_statistics();