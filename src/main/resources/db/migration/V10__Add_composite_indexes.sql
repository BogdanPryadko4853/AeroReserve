-- Для поиска рейсов по направлению и дате (самый частый запрос)
CREATE INDEX idx_flights_route_departure
    ON flights (route_id, departure_time)
    WHERE status = 'SCHEDULED';

-- Для поиска рейсов по авиакомпании и дате
CREATE INDEX idx_flights_airline_departure
    ON flights (airline_id, departure_time);

-- Для поиска рейсов по статусу и времени (админ-панель)
CREATE INDEX idx_flights_status_departure
    ON flights (status, departure_time);


-- Для поиска бронирований пользователя с сортировкой по дате
CREATE INDEX idx_bookings_user_status_date
    ON bookings(user_id, status, booking_date DESC);

-- Для аналитики: поиск бронирований по рейсу и статусу
CREATE INDEX idx_bookings_flight_status
    ON bookings(flight_id, status);

-- Для финансовой отчетности
CREATE INDEX idx_bookings_status_date_price
    ON bookings(status, booking_date, total_price);


-- Для поиска свободных мест при бронировании
CREATE INDEX idx_seats_flight_class_available
    ON seats(flight_id, seat_class, available)
    WHERE available = true;

-- Для проверки доступности конкретного места
CREATE INDEX idx_seats_flight_seat_available
    ON seats(flight_id, seat_number, available);

-- Для поиска маршрутов между городами
CREATE INDEX idx_routes_cities_price
    ON routes(departure_city_id, arrival_city_id, base_price);


-- Для поиска платежей по статусу и дате
CREATE INDEX idx_payments_status_created
    ON payments(status, created_at);

-- Для поиска платежей бронирования
CREATE INDEX idx_payments_booking_status
    ON payments(booking_id, status);