-- Добавление ограничений целостности

-- Внешние ключи с каскадным удалением где это уместно
ALTER TABLE cities
    ADD CONSTRAINT fk_cities_country
        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE;

ALTER TABLE flights
    ADD CONSTRAINT fk_flights_route
        FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE SET NULL;

ALTER TABLE flights
    ADD CONSTRAINT fk_flights_aircraft
        FOREIGN KEY (aircraft_id) REFERENCES aircrafts(id) ON DELETE SET NULL;

ALTER TABLE flights
    ADD CONSTRAINT fk_flights_airline
        FOREIGN KEY (airline_id) REFERENCES airlines(id) ON DELETE SET NULL;

ALTER TABLE routes
    ADD CONSTRAINT fk_routes_departure_city
        FOREIGN KEY (departure_city_id) REFERENCES cities(id) ON DELETE CASCADE;

ALTER TABLE routes
    ADD CONSTRAINT fk_routes_arrival_city
        FOREIGN KEY (arrival_city_id) REFERENCES cities(id) ON DELETE CASCADE;

ALTER TABLE seats
    ADD CONSTRAINT fk_seats_flight
        FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE;

ALTER TABLE bookings
    ADD CONSTRAINT fk_bookings_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE bookings
    ADD CONSTRAINT fk_bookings_flight
        FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE;

ALTER TABLE bookings
    ADD CONSTRAINT fk_bookings_seat
        FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE SET NULL;

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE;

ALTER TABLE tickets
    ADD CONSTRAINT fk_tickets_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE;



ALTER TABLE flights
    ADD CONSTRAINT chk_flights_positive_price
        CHECK (price >= 0);

ALTER TABLE aircrafts
    ADD CONSTRAINT chk_aircrafts_positive_seats
        CHECK (total_seats > 0 AND economy_seats >= 0 AND business_seats >= 0 AND first_class_seats >= 0);

ALTER TABLE payments
    ADD CONSTRAINT chk_payments_positive_amount
        CHECK (amount >= 0);

ALTER TABLE routes
    ADD CONSTRAINT chk_routes_different_cities
        CHECK (departure_city_id != arrival_city_id);