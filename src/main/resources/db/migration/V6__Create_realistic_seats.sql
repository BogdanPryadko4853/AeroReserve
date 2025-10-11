-- Создание реалистичных мест для каждого типа самолета

-- Функция для создания мест в зависимости от типа самолета
CREATE OR REPLACE FUNCTION create_seats_for_aircraft(flight_id BIGINT, aircraft_model VARCHAR)
RETURNS VOID AS $$
DECLARE
i INTEGER;
    seat_number TEXT;
    seat_class TEXT;
    row_letter TEXT;
    total_rows INTEGER;
    seats_per_row INTEGER;
    first_class_rows INTEGER;
    business_rows INTEGER;
    economy_rows INTEGER;
BEGIN
    -- Определяем конфигурацию мест в зависимости от модели самолета
CASE aircraft_model
        WHEN 'Boeing 737-800' THEN
            -- Boeing 737-800: 189 мест, конфигурация 3-3
            first_class_rows := 1;  -- 2 ряда по 4 места = 8 мест
            business_rows := 4;     -- 4 ряда по 6 мест = 24 места
            economy_rows := 27;     -- 27 рядов по 6 мест = 162 места
            seats_per_row := 6;

WHEN 'Boeing 777-300' THEN
            -- Boeing 777-300: 396 мест, конфигурация 3-4-3
            first_class_rows := 2;  -- 2 ряда по 6 мест = 12 мест
            business_rows := 5;     -- 5 рядов по 10 мест = 50 мест
            economy_rows := 34;     -- 34 ряда по 10 мест = 340 мест
            seats_per_row := 10;

WHEN 'Boeing 787 Dreamliner' THEN
            -- Boeing 787: 242 места, конфигурация 3-3-3
            first_class_rows := 2;  -- 2 ряда по 6 мест = 12 мест
            business_rows := 6;     -- 6 рядов по 7 мест = 42 места
            economy_rows := 27;     -- 27 рядов по 7 мест = 189 мест
            seats_per_row := 7;

WHEN 'Airbus A320' THEN
            -- Airbus A320: 180 мест, конфигурация 3-3
            first_class_rows := 1;  -- 1 ряд по 6 мест = 6 мест
            business_rows := 3;     -- 3 ряда по 6 мест = 18 мест
            economy_rows := 26;     -- 26 рядов по 6 мест = 156 мест
            seats_per_row := 6;

WHEN 'Airbus A330' THEN
            -- Airbus A330: 277 мест, конфигурация 2-4-2
            first_class_rows := 2;  -- 2 ряда по 6 мест = 12 мест
            business_rows := 5;     -- 5 рядов по 8 мест = 40 мест
            economy_rows := 29;     -- 29 рядов по 8 мест = 232 мест
            seats_per_row := 8;

WHEN 'Airbus A380' THEN
            -- Airbus A380: 525 мест, двухпалубный
            first_class_rows := 3;  -- 3 ряда по 10 мест = 30 мест (верхняя палуба)
            business_rows := 8;     -- 8 рядов по 12 мест = 96 мест (верхняя палуба)
            economy_rows := 40;     -- 40 рядов по 10 мест = 400 мест (нижняя палуба)
            seats_per_row := 10;

WHEN 'Boeing 747-8' THEN
            -- Boeing 747-8: 410 мест
            first_class_rows := 3;  -- 3 ряда по 8 мест = 24 места (нос)
            business_rows := 6;     -- 6 рядов по 10 мест = 60 мест (верхняя палуба)
            economy_rows := 33;     -- 33 ряда по 10 мест = 330 мест
            seats_per_row := 10;

WHEN 'Airbus A350' THEN
            -- Airbus A350: 325 мест, конфигурация 3-3-3
            first_class_rows := 3;  -- 3 ряда по 9 мест = 27 мест
            business_rows := 5;     -- 5 рядов по 9 мест = 45 мест
            economy_rows := 29;     -- 29 рядов по 9 мест = 261 мест
            seats_per_row := 9;

WHEN 'Embraer E195' THEN
            -- Embraer E195: 132 места, конфигурация 2-2
            first_class_rows := 0;  -- нет первого класса
            business_rows := 3;     -- 3 ряда по 4 места = 12 мест
            economy_rows := 30;     -- 30 рядов по 4 места = 120 мест
            seats_per_row := 4;

WHEN 'Bombardier CRJ-900' THEN
            -- Bombardier CRJ-900: 90 мест, конфигурация 2-2
            first_class_rows := 0;  -- нет первого класса
            business_rows := 3;     -- 3 ряда по 4 места = 12 мест
            economy_rows := 20;     -- 20 рядов по 4 места = 80 мест
            seats_per_row := 4;

WHEN 'Airbus A321neo' THEN
            -- Airbus A321neo: 240 мест, конфигурация 3-3
            first_class_rows := 2;  -- 2 ряда по 6 мест = 12 мест
            business_rows := 4;     -- 4 ряда по 6 мест = 24 мест
            economy_rows := 34;     -- 34 ряда по 6 мест = 204 мест
            seats_per_row := 6;

WHEN 'Boeing 767-300' THEN
            -- Boeing 767-300: 269 мест, конфигурация 2-3-2
            first_class_rows := 2;  -- 2 ряда по 6 мест = 12 мест
            business_rows := 5;     -- 5 рядов по 7 мест = 35 мест
            economy_rows := 32;     -- 32 ряда по 7 мест = 224 мест
            seats_per_row := 7;

ELSE
            -- По умолчанию: 150 мест, конфигурация 3-3
            first_class_rows := 1;
            business_rows := 3;
            economy_rows := 22;
            seats_per_row := 6;
END CASE;

    -- Буквы для обозначения мест (пропускаем I и O чтобы избежать путаницы)
    DECLARE
seat_letters TEXT[] := ARRAY['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M'];
BEGIN
        -- Первый класс
FOR i IN 1..first_class_rows LOOP
            FOR j IN 1..seats_per_row LOOP
                IF j <= array_length(seat_letters, 1) THEN
                    seat_number := i || seat_letters[j];
                    seat_class := 'FIRST';
INSERT INTO seats (flight_id, seat_number, seat_class, available)
VALUES (flight_id, seat_number, seat_class, true);
END IF;
END LOOP;
END LOOP;

        -- Бизнес класс
FOR i IN (first_class_rows + 1)..(first_class_rows + business_rows) LOOP
            FOR j IN 1..seats_per_row LOOP
                IF j <= array_length(seat_letters, 1) THEN
                    seat_number := i || seat_letters[j];
                    seat_class := 'BUSINESS';
INSERT INTO seats (flight_id, seat_number, seat_class, available)
VALUES (flight_id, seat_number, seat_class, true);
END IF;
END LOOP;
END LOOP;

        -- Эконом класс
FOR i IN (first_class_rows + business_rows + 1)..(first_class_rows + business_rows + economy_rows) LOOP
            FOR j IN 1..seats_per_row LOOP
                IF j <= array_length(seat_letters, 1) THEN
                    seat_number := i || seat_letters[j];
                    seat_class := 'ECONOMY';
INSERT INTO seats (flight_id, seat_number, seat_class, available)
VALUES (flight_id, seat_number, seat_class, true);
END IF;
END LOOP;
END LOOP;
END;
END;
$$ LANGUAGE plpgsql;

-- Создаем места для всех рейсов
DO $$
DECLARE
flight_record RECORD;
    aircraft_model TEXT;
BEGIN
FOR flight_record IN
SELECT f.id, a.model
FROM flights f
         JOIN aircrafts a ON f.aircraft_id = a.id
    LOOP
        -- Для каждого рейса создаем места согласно типу самолета
        PERFORM create_seats_for_aircraft(flight_record.id, flight_record.model);

RAISE NOTICE 'Созданы места для рейса %, самолет: %', flight_record.id, flight_record.model;
END LOOP;
END $$;

-- Проверяем созданные места
DO $$
DECLARE
flight_stats RECORD;
BEGIN
    RAISE NOTICE '=== СТАТИСТИКА СОЗДАННЫХ МЕСТ ===';

FOR flight_stats IN
SELECT
    f.id as flight_id,
    f.flight_number,
    a.model as aircraft_model,
    a.total_seats as expected_seats,
    COUNT(s.id) as created_seats,
    COUNT(CASE WHEN s.seat_class = 'FIRST' THEN 1 END) as first_class,
    COUNT(CASE WHEN s.seat_class = 'BUSINESS' THEN 1 END) as business,
    COUNT(CASE WHEN s.seat_class = 'ECONOMY' THEN 1 END) as economy
FROM flights f
         JOIN aircrafts a ON f.aircraft_id = a.id
         LEFT JOIN seats s ON f.id = s.flight_id
GROUP BY f.id, f.flight_number, a.model, a.total_seats
ORDER BY a.total_seats DESC
    LOOP
        RAISE NOTICE 'Рейс % (%) - %: создано %/% мест (First: %, Business: %, Economy: %)',
            flight_stats.flight_number,
            flight_stats.flight_id,
            flight_stats.aircraft_model,
            flight_stats.created_seats,
            flight_stats.expected_seats,
            flight_stats.first_class,
            flight_stats.business,
            flight_stats.economy;
END LOOP;

    RAISE NOTICE '=== СТАТИСТИКА ЗАВЕРШЕНА ===';
END $$;

-- Удаляем временную функцию
DROP FUNCTION create_seats_for_aircraft(BIGINT, VARCHAR);