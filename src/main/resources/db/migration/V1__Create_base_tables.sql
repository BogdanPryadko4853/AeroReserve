-- Создание таблицы countries
CREATE TABLE countries (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) UNIQUE NOT NULL,
                           code VARCHAR(10),
                           currency VARCHAR(10),
                           timezone VARCHAR(50)
);

-- Создание таблицы cities
CREATE TABLE cities (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        iata_code VARCHAR(3),
                        country_id BIGINT REFERENCES countries(id)
);

-- Создание таблицы airlines
CREATE TABLE airlines (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) UNIQUE NOT NULL,
                          code VARCHAR(10) NOT NULL,
                          country VARCHAR(100),
                          logo_url VARCHAR(500)
);

-- Создание таблица aircrafts
CREATE TABLE aircrafts (
                           id BIGSERIAL PRIMARY KEY,
                           model VARCHAR(255) UNIQUE NOT NULL,
                           manufacturer VARCHAR(255),
                           total_seats INTEGER NOT NULL,
                           economy_seats INTEGER NOT NULL,
                           business_seats INTEGER NOT NULL,
                           first_class_seats INTEGER NOT NULL
);

-- Создание таблицы routes
CREATE TABLE routes (
                        id BIGSERIAL PRIMARY KEY,
                        departure_city_id BIGINT REFERENCES cities(id),
                        arrival_city_id BIGINT REFERENCES cities(id),
                        base_price DECIMAL(10,2),
                        average_duration INTEGER,
                        distance INTEGER
);

-- Создание таблицы flights
CREATE TABLE flights (
                         id BIGSERIAL PRIMARY KEY,
                         flight_number VARCHAR(20) UNIQUE NOT NULL,
                         route_id BIGINT REFERENCES routes(id),
                         departure_time TIMESTAMP NOT NULL,
                         arrival_time TIMESTAMP NOT NULL,
                         price DECIMAL(10,2) NOT NULL,
                         status VARCHAR(20) DEFAULT 'SCHEDULED',
                         aircraft_id BIGINT REFERENCES aircrafts(id),
                         airline_id BIGINT REFERENCES airlines(id)
);

-- Создание таблицы users
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       phone_number VARCHAR(20),
                       role VARCHAR(20) DEFAULT 'USER',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы seats
CREATE TABLE seats (
                       id BIGSERIAL PRIMARY KEY,
                       flight_id BIGINT REFERENCES flights(id),
                       seat_number VARCHAR(10) NOT NULL,
                       seat_class VARCHAR(20) NOT NULL,
                       available BOOLEAN DEFAULT true
);

-- Создание таблицы bookings
CREATE TABLE bookings (
                          id BIGSERIAL PRIMARY KEY,
                          booking_number VARCHAR(50) UNIQUE NOT NULL,
                          user_id BIGINT REFERENCES users(id),
                          flight_id BIGINT REFERENCES flights(id),
                          seat_id BIGINT REFERENCES seats(id),  -- ✅ ПРАВИЛЬНО
                          passenger_name VARCHAR(255) NOT NULL,
                          total_price DECIMAL(10,2) NOT NULL,
                          status VARCHAR(30) DEFAULT 'PENDING_PAYMENT',
                          booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы payments
CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          stripe_payment_intent_id VARCHAR(255) UNIQUE,
                          booking_id BIGINT REFERENCES bookings(id),
                          amount DECIMAL(10,2) NOT NULL,
                          currency VARCHAR(10) DEFAULT 'USD',
                          status VARCHAR(50),
                          client_secret VARCHAR(255),
                          payment_method VARCHAR(50),
                          last_payment_error TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы tickets
CREATE TABLE tickets (
                         id BIGSERIAL PRIMARY KEY,
                         ticket_number VARCHAR(50) UNIQUE NOT NULL,
                         booking_id BIGINT REFERENCES bookings(id),
                         qr_code_url VARCHAR(500),
                         boarding_pass_url VARCHAR(500),
                         status VARCHAR(20) DEFAULT 'ISSUED',
                         issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         boarding_time TIMESTAMP
);

-- Создание таблицы flight_statistics
CREATE TABLE flight_statistics (
                                   id BIGSERIAL PRIMARY KEY,
                                   flight_id BIGINT REFERENCES flights(id),
                                   total_bookings INTEGER DEFAULT 0,
                                   completed_flights INTEGER DEFAULT 0,
                                   cancelled_flights INTEGER DEFAULT 0,
                                   average_delay_minutes DECIMAL(5,2) DEFAULT 0.0,
                                   on_time_performance DECIMAL(5,2) DEFAULT 100.0,
                                   customer_satisfaction_score DECIMAL(3,2) DEFAULT 0.0,
                                   total_passengers INTEGER DEFAULT 0,
                                   load_factor DECIMAL(5,2) DEFAULT 0.0,
                                   last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);