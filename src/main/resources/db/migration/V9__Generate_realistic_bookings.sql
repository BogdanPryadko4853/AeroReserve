-- Генерация реалистичных бронирований с гарантированно уникальными номерами
INSERT INTO bookings (booking_number, user_id, flight_id, seat_id, passenger_name, total_price, status, booking_date)
WITH booking_data AS (
    SELECT
        -- Гарантированно уникальный номер бронирования с временной меткой
        'BK' ||
        TO_CHAR(EXTRACT(EPOCH FROM NOW())::bigint, 'FM0000000000') ||
        LPAD(floor(random() * 10000)::text, 4, '0') ||
        seq::text as booking_number,

        -- Случайный пользователь
        u.id as user_id,

        -- Случайный рейс из будущего
        f.id as flight_id,

        -- Случайное доступное место на этом рейсе
        s.id as seat_id,

        -- Имя пассажира
        CASE
            WHEN random() < 0.7 THEN u.first_name || ' ' || u.last_name
            ELSE (ARRAY['Alex','Brian','Chris','Diana','Ethan','Fiona','George','Hannah','Ian','Julia',
                  'Kevin','Laura','Mike','Nina','Oscar','Paula','Quinn','Rachel','Steve','Tina'])[(floor(random() * 20) + 1)] || ' ' ||
                 u.last_name
            END as passenger_name,

        -- Цена с учетом класса обслуживания
        CASE s.seat_class
            WHEN 'FIRST' THEN f.price * 3.5
            WHEN 'BUSINESS' THEN f.price * 2.2
            ELSE f.price * 1.0
            END * (0.9 + random() * 0.2) as total_price,

        -- Статус бронирования
        CASE
            WHEN random() < 0.6 THEN 'CONFIRMED'
            WHEN random() < 0.8 THEN 'PENDING_PAYMENT'
            WHEN random() < 0.9 THEN 'CANCELLED'
            ELSE 'REFUNDED'
            END as status,

        -- Дата бронирования
        f.departure_time - (1 + random() * 180) * INTERVAL '1 day' as booking_date,

     -- Для устранения дубликатов
    ROW_NUMBER() OVER (ORDER BY random()) as rn

FROM generate_series(1, 2000) as seq  -- Генерируем больше, чем нужно
    CROSS JOIN LATERAL (
    SELECT id, first_name, last_name
    FROM users
    ORDER BY random()
    LIMIT 1
    ) u
    CROSS JOIN LATERAL (
    SELECT f.id, f.departure_time, f.price
    FROM flights f
    WHERE f.departure_time > CURRENT_TIMESTAMP
    AND EXISTS (SELECT 1 FROM seats s WHERE s.flight_id = f.id AND s.available = true)
    ORDER BY random()
    LIMIT 1
    ) f
    CROSS JOIN LATERAL (
    SELECT s.id, s.seat_class
    FROM seats s
    WHERE s.flight_id = f.id AND s.available = true
    ORDER BY random()
    LIMIT 1
    ) s
WHERE NOT EXISTS (SELECT 1 FROM bookings b WHERE b.seat_id = s.id)
    )
SELECT
    booking_number,
    user_id,
    flight_id,
    seat_id,
    passenger_name,
    total_price,
    status,
    booking_date
FROM booking_data
WHERE rn <= 1000  -- Ограничиваем до 1000 записей
    ON CONFLICT (booking_number) DO NOTHING;  -- На всякий случай

-- Обновляем статус мест на "занятые" для подтвержденных бронирований
UPDATE seats s
SET available = false
    FROM bookings b
WHERE b.seat_id = s.id
  AND b.status IN ('CONFIRMED', 'PENDING_PAYMENT');

-- Логируем результат
DO $$
DECLARE
total_bookings INTEGER;
    confirmed_bookings INTEGER;
    pending_bookings INTEGER;
    cancelled_bookings INTEGER;
    refunded_bookings INTEGER;
BEGIN
SELECT COUNT(*) INTO total_bookings FROM bookings;
SELECT COUNT(*) INTO confirmed_bookings FROM bookings WHERE status = 'CONFIRMED';
SELECT COUNT(*) INTO pending_bookings FROM bookings WHERE status = 'PENDING_PAYMENT';
SELECT COUNT(*) INTO cancelled_bookings FROM bookings WHERE status = 'CANCELLED';
SELECT COUNT(*) INTO refunded_bookings FROM bookings WHERE status = 'REFUNDED';

RAISE NOTICE '=== СТАТИСТИКА БРОНИРОВАНИЙ ===';
    RAISE NOTICE 'Всего бронирований: %', total_bookings;
    RAISE NOTICE 'Подтвержденных: %', confirmed_bookings;
    RAISE NOTICE 'Ожидают оплаты: %', pending_bookings;
    RAISE NOTICE 'Отмененных: %', cancelled_bookings;
    RAISE NOTICE 'Возвратов: %', refunded_bookings;
    RAISE NOTICE '==============================';
END $$;