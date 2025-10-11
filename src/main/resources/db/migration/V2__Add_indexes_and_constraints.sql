-- Только необходимые индексы для производительности

-- Индексы для часто используемых поисковых полей
CREATE INDEX idx_cities_name ON cities(name);
CREATE INDEX idx_cities_country_id ON cities(country_id);

-- Индексы для авиакомпаний (поиск по коду и имени)
CREATE INDEX idx_airlines_code ON airlines(code);

-- Индексы для рейсов (самые важные для поиска)
CREATE INDEX idx_flights_flight_number ON flights(flight_number);
CREATE INDEX idx_flights_departure_time ON flights(departure_time);
CREATE INDEX idx_flights_status ON flights(status);
CREATE INDEX idx_flights_route_id ON flights(route_id);

-- Индексы для пользователей (поиск по email)
CREATE INDEX idx_users_email ON users(email);

-- Индексы для мест в самолете (бронирование)
CREATE INDEX idx_seats_flight_id ON seats(flight_id);
CREATE INDEX idx_seats_available ON seats(available);
CREATE UNIQUE INDEX idx_seats_flight_seat_unique ON seats(flight_id, seat_number);

-- Индексы для бронирований (поиск по пользователю и статусу)
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_booking_date ON bookings(booking_date);

-- Индексы для платежей (отслеживание статусов)
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_booking_id ON payments(booking_id);

-- Индексы для маршрутов (уникальность направления)
CREATE UNIQUE INDEX idx_routes_departure_arrival_unique ON routes(departure_city_id, arrival_city_id);