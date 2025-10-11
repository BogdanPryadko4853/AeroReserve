-- Создание большого количества тестовых рейсов
-- Рейсы на ближайшие 30 дней с разными временами вылета

-- Функция для генерации случайного времени в пределах дня
CREATE OR REPLACE FUNCTION random_time_between(start_hour INT, end_hour INT)
RETURNS TIME AS $$
BEGIN
RETURN make_time(
        (start_hour + floor(random() * (end_hour - start_hour))::INT),
        (floor(random() * 4) * 15)::INT, -- 0, 15, 30, 45 минут
        0
       );
END;
$$ LANGUAGE plpgsql;

-- Вставка рейсов
INSERT INTO flights (flight_number, route_id, departure_time, arrival_time, price, status, aircraft_id, airline_id)
VALUES

-- РЕЙСЫ НА СЕГОДНЯ И ЗАВТРА
-- NYC -> LON (рейсы в Лондон)
('BA117', 1,
 (CURRENT_DATE + 1 + random_time_between(18, 22))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(6, 9))::timestamp,
 525.00, 'SCHEDULED', 2, 5),
('DL245', 1,
 (CURRENT_DATE + random_time_between(20, 23))::timestamp,
 (CURRENT_DATE + 1 + random_time_between(8, 11))::timestamp,
 510.00, 'SCHEDULED', 3, 7),
('AR101', 1,
 (CURRENT_DATE + 2 + random_time_between(9, 12))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(21, 23))::timestamp,
 499.99, 'SCHEDULED', 2, 1),

-- NYC -> PAR (рейсы в Париж)
('AF389', 2,
 (CURRENT_DATE + 1 + random_time_between(16, 19))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(5, 7))::timestamp,
 475.00, 'SCHEDULED', 5, 6),
('DL567', 2,
 (CURRENT_DATE + random_time_between(21, 23))::timestamp,
 (CURRENT_DATE + 1 + random_time_between(10, 12))::timestamp,
 485.00, 'SCHEDULED', 3, 7),

-- LON -> TYO (рейсы в Токио)
('JL045', 3,
 (CURRENT_DATE + 2 + random_time_between(11, 13))::timestamp,
 (CURRENT_DATE + 3 + random_time_between(7, 9))::timestamp,
 925.00, 'SCHEDULED', 6, 13),
('BA005', 3,
 (CURRENT_DATE + 1 + random_time_between(13, 15))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(9, 11))::timestamp,
 899.99, 'SCHEDULED', 2, 5),

-- PAR -> DXB (рейсы в Дубай)
('EK076', 4,
 (CURRENT_DATE + random_time_between(14, 16))::timestamp,
 (CURRENT_DATE + random_time_between(23, 24))::timestamp,
 725.00, 'SCHEDULED', 4, 4),
('AF132', 4,
 (CURRENT_DATE + 3 + random_time_between(10, 12))::timestamp,
 (CURRENT_DATE + 3 + random_time_between(19, 21))::timestamp,
 699.99, 'SCHEDULED', 5, 6),

-- TYO -> SYD (рейсы в Сидней)
('QF025', 5,
 (CURRENT_DATE + 2 + random_time_between(9, 11))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(22, 24))::timestamp,
 825.00, 'SCHEDULED', 3, 11),
('JL051', 5,
 (CURRENT_DATE + 1 + random_time_between(20, 22))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(9, 11))::timestamp,
 799.99, 'SCHEDULED', 2, 13),

-- DXB -> SIN (рейсы в Сингапур)
('EK354', 6,
 (CURRENT_DATE + random_time_between(2, 4))::timestamp,
 (CURRENT_DATE + random_time_between(14, 16))::timestamp,
 575.00, 'SCHEDULED', 4, 4),
('SQ492', 6,
 (CURRENT_DATE + 4 + random_time_between(21, 23))::timestamp,
 (CURRENT_DATE + 5 + random_time_between(9, 11))::timestamp,
 549.99, 'SCHEDULED', 5, 8),

-- SVO -> DXB (рейсы из Москвы в Дубай)
('EK134', 7,
 (CURRENT_DATE + 2 + random_time_between(16, 18))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(22, 24))::timestamp,
 425.00, 'SCHEDULED', 4, 4),
('SU522', 7,
 (CURRENT_DATE + 1 + random_time_between(8, 10))::timestamp,
 (CURRENT_DATE + 1 + random_time_between(14, 16))::timestamp,
 399.99, 'SCHEDULED', 3, 2),

-- LAX -> TYO (рейсы из Лос-Анджелеса в Токио)
('JL061', 8,
 (CURRENT_DATE + 3 + random_time_between(11, 13))::timestamp,
 (CURRENT_DATE + 4 + random_time_between(15, 17))::timestamp,
 875.00, 'SCHEDULED', 6, 13),
('AA169', 8,
 (CURRENT_DATE + 2 + random_time_between(14, 16))::timestamp,
 (CURRENT_DATE + 3 + random_time_between(18, 20))::timestamp,
 849.99, 'SCHEDULED', 2, 7),

-- ORD -> LON (рейсы из Чикаго в Лондон)
('BA295', 9,
 (CURRENT_DATE + 1 + random_time_between(18, 20))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(8, 10))::timestamp,
 555.00, 'SCHEDULED', 3, 5),
('UA958', 9,
 (CURRENT_DATE + 4 + random_time_between(20, 22))::timestamp,
 (CURRENT_DATE + 5 + random_time_between(10, 12))::timestamp,
 529.99, 'SCHEDULED', 2, 7),

-- YYZ -> FRA (рейсы из Торонто во Франкфурт)
('LH492', 10,
 (CURRENT_DATE + 2 + random_time_between(17, 19))::timestamp,
 (CURRENT_DATE + 3 + random_time_between(7, 9))::timestamp,
 655.00, 'SCHEDULED', 5, 3),
('AC874', 10,
 (CURRENT_DATE + 1 + random_time_between(21, 23))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(11, 13))::timestamp,
 629.99, 'SCHEDULED', 3, 12),

-- PEK -> SIN (рейсы из Пекина в Сингапур)
('CA975', 11,
 (CURRENT_DATE + random_time_between(8, 10))::timestamp,
 (CURRENT_DATE + random_time_between(15, 17))::timestamp,
 455.00, 'SCHEDULED', 4, 1),
('SQ802', 11,
 (CURRENT_DATE + 5 + random_time_between(14, 16))::timestamp,
 (CURRENT_DATE + 5 + random_time_between(21, 23))::timestamp,
 429.99, 'SCHEDULED', 3, 8),

-- FRA -> IST (рейсы из Франкфурта в Стамбул)
('TK1592', 12,
 (CURRENT_DATE + 3 + random_time_between(6, 8))::timestamp,
 (CURRENT_DATE + 3 + random_time_between(10, 12))::timestamp,
 355.00, 'SCHEDULED', 1, 10),
('LH1294', 12,
 (CURRENT_DATE + 2 + random_time_between(19, 21))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(23, 24))::timestamp,
 329.99, 'SCHEDULED', 4, 3),

-- SYD -> LAX (рейсы из Сиднея в Лос-Анджелес)
('QF011', 13,
 (CURRENT_DATE + 4 + random_time_between(9, 11))::timestamp,
 (CURRENT_DATE + 4 + random_time_between(6, 8))::timestamp, -- Пересечение часовых поясов
 1245.00, 'SCHEDULED', 6, 11),
('UA839', 13,
 (CURRENT_DATE + 2 + random_time_between(16, 18))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(13, 15))::timestamp, -- Пересечение часовых поясов
 1199.99, 'SCHEDULED', 2, 7),

-- IST -> DXB (рейсы из Стамбула в Дубай)
('EK121', 14,
 (CURRENT_DATE + 1 + random_time_between(1, 3))::timestamp,
 (CURRENT_DATE + 1 + random_time_between(6, 8))::timestamp,
 375.00, 'SCHEDULED', 4, 4),
('TK763', 14,
 (CURRENT_DATE + 3 + random_time_between(13, 15))::timestamp,
 (CURRENT_DATE + 3 + random_time_between(18, 20))::timestamp,
 349.99, 'SCHEDULED', 3, 10),

-- PAR -> FRA (короткие рейсы по Европе)
('LH1038', 15,
 (CURRENT_DATE + random_time_between(7, 9))::timestamp,
 (CURRENT_DATE + random_time_between(9, 11))::timestamp,
 225.00, 'SCHEDULED', 1, 3),
('AF1512', 15,
 (CURRENT_DATE + 6 + random_time_between(17, 19))::timestamp,
 (CURRENT_DATE + 6 + random_time_between(19, 21))::timestamp,
 199.99, 'SCHEDULED', 4, 6),

-- ОБРАТНЫЕ РЕЙСЫ И ДОПОЛНИТЕЛЬНЫЕ

-- LON -> NYC (обратные рейсы)
('BA116', 1,
 (CURRENT_DATE + 2 + random_time_between(10, 12))::timestamp,
 (CURRENT_DATE + 2 + random_time_between(13, 15))::timestamp,
 515.00, 'SCHEDULED', 2, 5),
('VS010', 1,
 (CURRENT_DATE + 5 + random_time_between(14, 16))::timestamp,
 (CURRENT_DATE + 5 + random_time_between(17, 19))::timestamp,
 505.00, 'SCHEDULED', 3, 5),

-- PAR -> NYC
('AF388', 2,
 (CURRENT_DATE + 4 + random_time_between(12, 14))::timestamp,
 (CURRENT_DATE + 4 + random_time_between(15, 17))::timestamp,
 465.00, 'SCHEDULED', 5, 6),

-- TYO -> LON
('BA006', 3,
 (CURRENT_DATE + 7 + random_time_between(10, 12))::timestamp,
 (CURRENT_DATE + 7 + random_time_between(15, 17))::timestamp,
 915.00, 'SCHEDULED', 6, 5),

-- ДОПОЛНИТЕЛЬНЫЕ РЕЙСЫ ДЛЯ ПОПУЛЯРНЫХ НАПРАВЛЕНИЙ

-- NYC -> LON (еще рейсы)
('UA925', 1,
 (CURRENT_DATE + 8 + random_time_between(19, 21))::timestamp,
 (CURRENT_DATE + 9 + random_time_between(7, 9))::timestamp,
 535.00, 'SCHEDULED', 2, 7),
('AR201', 1,
 (CURRENT_DATE + 10 + random_time_between(15, 17))::timestamp,
 (CURRENT_DATE + 11 + random_time_between(3, 5))::timestamp,
 495.00, 'SCHEDULED', 3, 1),

-- NYC -> PAR (еще рейсы)
('DL246', 2,
 (CURRENT_DATE + 7 + random_time_between(21, 23))::timestamp,
 (CURRENT_DATE + 8 + random_time_between(11, 13))::timestamp,
 475.00, 'SCHEDULED', 4, 7),
('AR301', 2,
 (CURRENT_DATE + 12 + random_time_between(14, 16))::timestamp,
 (CURRENT_DATE + 13 + random_time_between(4, 6))::timestamp,
 455.00, 'SCHEDULED', 5, 1),

-- ДОБАВЛЯЕМ ЕЩЕ БОЛЬШЕ РЕЙСОВ ДЛЯ РАЗНЫХ ДАТ...

-- Рейсы на следующие 30 дней
('LH401', 10,
 (CURRENT_DATE + 15 + random_time_between(16, 18))::timestamp,
 (CURRENT_DATE + 16 + random_time_between(6, 8))::timestamp,
 645.00, 'SCHEDULED', 3, 3),
('EK412', 4,
 (CURRENT_DATE + 18 + random_time_between(2, 4))::timestamp,
 (CURRENT_DATE + 18 + random_time_between(7, 9))::timestamp,
 715.00, 'SCHEDULED', 4, 4),
('SQ215', 6,
 (CURRENT_DATE + 20 + random_time_between(22, 24))::timestamp,
 (CURRENT_DATE + 21 + random_time_between(10, 12))::timestamp,
 565.00, 'SCHEDULED', 5, 8),
('JL078', 5,
 (CURRENT_DATE + 22 + random_time_between(8, 10))::timestamp,
 (CURRENT_DATE + 22 + random_time_between(21, 23))::timestamp,
 815.00, 'SCHEDULED', 6, 13),
('QF032', 13,
 (CURRENT_DATE + 25 + random_time_between(11, 13))::timestamp,
 (CURRENT_DATE + 25 + random_time_between(8, 10))::timestamp,
 1225.00, 'SCHEDULED', 2, 11),
('TK189', 12,
 (CURRENT_DATE + 28 + random_time_between(15, 17))::timestamp,
 (CURRENT_DATE + 28 + random_time_between(19, 21))::timestamp,
 345.00, 'SCHEDULED', 1, 10),
('AC189', 10,
 (CURRENT_DATE + 30 + random_time_between(20, 22))::timestamp,
 (CURRENT_DATE + 31 + random_time_between(10, 12))::timestamp,
 635.00, 'SCHEDULED', 3, 12);

-- Удаляем временную функцию
DROP FUNCTION random_time_between(INT, INT);