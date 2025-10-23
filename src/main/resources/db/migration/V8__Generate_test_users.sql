-- Генерация 1000 реалистичных пользователей
INSERT INTO users (email, password, first_name, last_name, phone_number, role, created_at)
SELECT
    -- Уникальные email
    'user_' || seq || '@test.com' as email,
    -- Захешированные пароли (bcrypt для "password123")
    '$2a$10$N9qo8uLOickgx2ZMRZoMye.KbJX8JYV.HdYFZ2qVx7QrH/Gz.4QOq' as password,
    -- Реалистичные имена
    (ARRAY['James','John','Robert','Michael','William','David','Richard','Joseph','Thomas','Charles',
     'Mary','Patricia','Jennifer','Linda','Elizabeth','Barbara','Susan','Jessica','Sarah','Karen',
     'Daniel','Matthew','Anthony','Mark','Donald','Steven','Paul','Andrew','Joshua','Kenneth',
     'Nancy','Lisa','Betty','Helen','Sandra','Donna','Carol','Ruth','Sharon','Michelle',
     'Kevin','Brian','George','Edward','Ronald','Timothy','Jason','Jeffrey','Ryan','Jacob',
     'Emily','Emma','Olivia','Ava','Isabella','Sophia','Charlotte','Mia','Amelia','Harper'])[(seq % 60) + 1] as first_name,

    -- Реалистичные фамилии
    (ARRAY['Smith','Johnson','Williams','Brown','Jones','Garcia','Miller','Davis','Rodriguez','Martinez',
          'Hernandez','Lopez','Gonzalez','Wilson','Anderson','Thomas','Taylor','Moore','Jackson','Martin',
          'Lee','Perez','Thompson','White','Harris','Sanchez','Clark','Ramirez','Lewis','Robinson',
          'Walker','Young','Allen','King','Wright','Scott','Torres','Nguyen','Hill','Flores',
          'Green','Adams','Nelson','Baker','Hall','Rivera','Campbell','Mitchell','Carter','Roberts'])[(seq % 50) + 1] as last_name,

    -- Телефоны в формате +1-XXX-XXX-XXXX
    '+1-' ||
    LPAD((floor(random() * 900) + 100)::text, 3, '0') || '-' ||
    LPAD((floor(random() * 900) + 100)::text, 3, '0') || '-' ||
    LPAD((floor(random() * 10000))::text, 4, '0') as phone_number,

    -- 95% пользователей, 5% админов
    CASE WHEN random() < 0.05 THEN 'ADMIN' ELSE 'USER' END as role,

    -- Дата регистрации за последние 2 года
    CURRENT_TIMESTAMP - (random() * 730 * INTERVAL '1 day') as created_at

FROM generate_series(1, 1000) as seq;