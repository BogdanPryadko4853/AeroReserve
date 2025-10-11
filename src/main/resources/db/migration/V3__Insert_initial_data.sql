-- Добавление начальных данных

-- Страны (из DataInitializer)
INSERT INTO countries (name, code, currency, timezone) VALUES
                                                           ('United States', 'US', 'USD', 'America/New_York'),
                                                           ('United Kingdom', 'GB', 'GBP', 'Europe/London'),
                                                           ('France', 'FR', 'EUR', 'Europe/Paris'),
                                                           ('Germany', 'DE', 'EUR', 'Europe/Berlin'),
                                                           ('United Arab Emirates', 'AE', 'AED', 'Asia/Dubai'),
                                                           ('Japan', 'JP', 'JPY', 'Asia/Tokyo'),
                                                           ('Australia', 'AU', 'AUD', 'Australia/Sydney'),
                                                           ('Singapore', 'SG', 'SGD', 'Asia/Singapore'),
                                                           ('Russia', 'RU', 'RUB', 'Europe/Moscow'),
                                                           ('Canada', 'CA', 'CAD', 'America/Toronto'),
                                                           ('China', 'CN', 'CNY', 'Asia/Shanghai'),
                                                           ('Turkey', 'TR', 'TRY', 'Europe/Istanbul'),
                                                           ('Netherlands', 'NL', 'EUR', 'Europe/Amsterdam'),
                                                           ('Qatar', 'QA', 'QAR', 'Asia/Qatar'),
                                                           ('South Korea', 'KR', 'KRW', 'Asia/Seoul');

-- Города (из DataInitializer)
INSERT INTO cities (name, iata_code, country_id) VALUES
                                                     ('New York', 'NYC', 1),
                                                     ('London', 'LON', 2),
                                                     ('Paris', 'PAR', 3),
                                                     ('Berlin', 'BER', 4),
                                                     ('Dubai', 'DXB', 5),
                                                     ('Tokyo', 'TYO', 6),
                                                     ('Sydney', 'SYD', 7),
                                                     ('Singapore', 'SIN', 8),
                                                     ('Moscow', 'SVO', 9),
                                                     ('Los Angeles', 'LAX', 1),
                                                     ('Chicago', 'ORD', 1),
                                                     ('Toronto', 'YYZ', 10),
                                                     ('Beijing', 'PEK', 11),
                                                     ('Shanghai', 'PVG', 11),
                                                     ('Frankfurt', 'FRA', 4),
                                                     ('Amsterdam', 'AMS', 13),
                                                     ('Istanbul', 'IST', 12);

-- Авиакомпании (из DataInitializer)
INSERT INTO airlines (name, code, country, logo_url) VALUES
                                                         ('AeroReserve', 'AR', 'International', '/logos/aeroreserve.png'),
                                                         ('Aeroflot', 'SU', 'Russia', '/logos/aeroflot.png'),
                                                         ('Lufthansa', 'LH', 'Germany', '/logos/lufthansa.png'),
                                                         ('Emirates', 'EK', 'UAE', '/logos/emirates.png'),
                                                         ('British Airways', 'BA', 'United Kingdom', '/logos/british_airways.png'),
                                                         ('Air France', 'AF', 'France', '/logos/air_france.png'),
                                                         ('Delta Air Lines', 'DL', 'United States', '/logos/delta.png'),
                                                         ('Singapore Airlines', 'SQ', 'Singapore', '/logos/singapore_airlines.png'),
                                                         ('Qatar Airways', 'QR', 'Qatar', '/logos/qatar_airways.png'),
                                                         ('Turkish Airlines', 'TK', 'Turkey', '/logos/turkish_airlines.png'),
                                                         ('Qantas', 'QF', 'Australia', '/logos/qantas.png'),
                                                         ('Air Canada', 'AC', 'Canada', '/logos/air_canada.png'),
                                                         ('Japan Airlines', 'JL', 'Japan', '/logos/japan_airlines.png'),
                                                         ('Korean Air', 'KE', 'South Korea', '/logos/korean_air.png');

-- Самолеты (из DataInitializer)
INSERT INTO aircrafts (model, manufacturer, total_seats, economy_seats, business_seats, first_class_seats) VALUES
                                                                                                               ('Boeing 737-800', 'Boeing', 162, 162, 0, 0),
                                                                                                               ('Boeing 777-300', 'Boeing', 276, 220, 48, 8),
                                                                                                               ('Boeing 787 Dreamliner', 'Boeing', 224, 180, 32, 12),
                                                                                                               ('Airbus A320', 'Airbus', 150, 150, 0, 0),
                                                                                                               ('Airbus A330', 'Airbus', 248, 200, 36, 12),
                                                                                                               ('Airbus A380', 'Airbus', 500, 400, 80, 20),
                                                                                                               ('Boeing 747-8', 'Boeing', 425, 350, 60, 15),
                                                                                                               ('Airbus A350', 'Airbus', 338, 280, 40, 18),
                                                                                                               ('Embraer E195', 'Embraer', 120, 120, 0, 0);

-- Маршруты (из DataInitializer)
INSERT INTO routes (departure_city_id, arrival_city_id, base_price, average_duration, distance) VALUES
                                                                                                    (1, 2, 499.99, 420, 5567),   -- NYC to LON
                                                                                                    (1, 3, 459.99, 435, 5834),   -- NYC to PAR
                                                                                                    (2, 6, 899.99, 720, 9560),   -- LON to TYO
                                                                                                    (3, 5, 699.99, 390, 5167),   -- PAR to DXB
                                                                                                    (6, 7, 799.99, 585, 7821),   -- TYO to SYD
                                                                                                    (5, 8, 549.99, 465, 5846),   -- DXB to SIN
                                                                                                    (9, 5, 399.99, 300, 3724),   -- SVO to DXB
                                                                                                    (10, 6, 849.99, 600, 8800),  -- LAX to TYO
                                                                                                    (11, 2, 529.99, 450, 6400),  -- ORD to LON
                                                                                                    (12, 15, 629.99, 480, 6200), -- YYZ to FRA
                                                                                                    (13, 8, 429.99, 360, 4400),  -- PEK to SIN
                                                                                                    (15, 17, 329.99, 180, 2200), -- FRA to IST
                                                                                                    (7, 10, 1199.99, 840, 12000),-- SYD to LAX
                                                                                                    (17, 5, 349.99, 240, 3100),  -- IST to DXB
                                                                                                    (3, 15, 199.99, 90, 450);    -- PAR to FRA