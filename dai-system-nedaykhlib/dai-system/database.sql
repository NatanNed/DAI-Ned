DROP TABLE IF EXISTS fines CASCADE;
DROP TABLE IF EXISTS driving_permissions CASCADE;
DROP TABLE IF EXISTS license_plates CASCADE;
DROP TABLE IF EXISTS vehicles CASCADE;
DROP TABLE IF EXISTS vehicle_brands CASCADE;
DROP TABLE IF EXISTS persons CASCADE;

CREATE TABLE persons (
    person_id SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    birth_date DATE,
    driver_license VARCHAR(50) UNIQUE,
    phone VARCHAR(30)
);

CREATE TABLE vehicle_brands (
    brand_id SERIAL PRIMARY KEY,
    brand_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE vehicles (
    vehicle_id SERIAL PRIMARY KEY,
    vin VARCHAR(50) NOT NULL UNIQUE,
    brand_id INT NOT NULL REFERENCES vehicle_brands(brand_id),
    model VARCHAR(100) NOT NULL,
    production_year INT CHECK (production_year >= 1950),
    owner_id INT NOT NULL REFERENCES persons(person_id)
);

CREATE TABLE license_plates (
    plate_id SERIAL PRIMARY KEY,
    vehicle_id INT NOT NULL REFERENCES vehicles(vehicle_id),
    plate_number VARCHAR(20) NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE,
    is_wanted BOOLEAN DEFAULT FALSE
);

CREATE TABLE driving_permissions (
    permission_id SERIAL PRIMARY KEY,
    vehicle_id INT NOT NULL REFERENCES vehicles(vehicle_id),
    person_id INT NOT NULL REFERENCES persons(person_id),
    valid_from DATE NOT NULL,
    valid_to DATE,
    permission_type VARCHAR(50) DEFAULT 'Довіреність'
);

CREATE TABLE fines (
    fine_id SERIAL PRIMARY KEY,
    vehicle_id INT NOT NULL REFERENCES vehicles(vehicle_id),
    driver_id INT NOT NULL REFERENCES persons(person_id),
    fine_date DATE NOT NULL,
    amount NUMERIC(10,2) NOT NULL CHECK (amount > 0),
    violation_type VARCHAR(150) NOT NULL,
    is_paid BOOLEAN DEFAULT FALSE,
    is_accident BOOLEAN DEFAULT FALSE
);

INSERT INTO persons (full_name, birth_date, driver_license, phone) VALUES
('Недайхліб Натан Сергійович', '2004-03-14', 'DL100001', '+380501111111'),
('Іваненко Олександр Петрович', '1985-06-12', 'DL100002', '+380502222222'),
('Петренко Марина Ігорівна', '1990-02-22', 'DL100003', '+380503333333'),
('Коваленко Дмитро Андрійович', '1979-11-05', 'DL100004', '+380504444444'),
('Шевченко Олена Василівна', '1995-09-18', 'DL100005', '+380505555555');

INSERT INTO vehicle_brands (brand_name) VALUES
('Toyota'),
('Volkswagen'),
('BMW'),
('Ford'),
('Renault');

INSERT INTO vehicles (vin, brand_id, model, production_year, owner_id) VALUES
('VIN00000000000001', 1, 'Corolla', 2018, 1),
('VIN00000000000002', 2, 'Golf', 2016, 2),
('VIN00000000000003', 3, 'X5', 2020, 3),
('VIN00000000000004', 4, 'Focus', 2017, 4),
('VIN00000000000005', 5, 'Megane', 2019, 5);

INSERT INTO license_plates (vehicle_id, plate_number, valid_from, valid_to, is_wanted) VALUES
(1, 'AA6677BB', '2024-01-01', NULL, FALSE),
(2, 'BM1234CK', '2023-05-10', NULL, FALSE),
(3, 'AX6670HP', '2022-03-12', NULL, FALSE),
(4, 'KA5522OP', '2021-07-01', NULL, FALSE),
(5, 'BC9088TA', '2024-02-15', NULL, FALSE),
(1, 'AA1111AA', '2021-01-01', '2023-12-31', TRUE);

INSERT INTO driving_permissions (vehicle_id, person_id, valid_from, valid_to, permission_type) VALUES
(1, 1, '2024-01-01', NULL, 'Власник'),
(1, 2, '2025-01-01', '2026-12-31', 'Довіреність'),
(2, 2, '2023-05-10', NULL, 'Власник'),
(3, 3, '2022-03-12', NULL, 'Власник'),
(4, 4, '2021-07-01', NULL, 'Власник'),
(5, 5, '2024-02-15', NULL, 'Власник');

INSERT INTO fines (vehicle_id, driver_id, fine_date, amount, violation_type, is_paid, is_accident) VALUES
(1, 1, '2026-05-01', 500.00, 'Перевищення швидкості', FALSE, FALSE),
(1, 2, '2026-05-05', 850.00, 'Порушення правил зупинки', TRUE, FALSE),
(2, 2, '2026-05-10', 1200.00, 'Проїзд на червоне світло', FALSE, FALSE),
(3, 3, '2026-04-18', 3400.00, 'ДТП', FALSE, TRUE),
(4, 5, '2026-05-15', 2500.00, 'ДТП без права керування', FALSE, TRUE),
(5, 5, '2026-03-20', 600.00, 'Неправильне паркування', TRUE, FALSE);

-- Типові запити з курсового завдання

-- 1. Власники транспортних засобів, номерний знак яких містить послідовність 667.
SELECT DISTINCT p.full_name, lp.plate_number
FROM vehicles v
JOIN persons p ON v.owner_id = p.person_id
JOIN license_plates lp ON v.vehicle_id = lp.vehicle_id
WHERE lp.plate_number LIKE '%667%'
  AND lp.valid_to IS NULL;

-- 2. Сума несплачених штрафів у поточному місяці.
SELECT SUM(amount) AS unpaid_fines_sum
FROM fines
WHERE is_paid = FALSE
  AND EXTRACT(MONTH FROM fine_date) = EXTRACT(MONTH FROM CURRENT_DATE)
  AND EXTRACT(YEAR FROM fine_date) = EXTRACT(YEAR FROM CURRENT_DATE);

-- 3. Статистика порушень за марками автомобілів та місяцями.
SELECT vb.brand_name,
       EXTRACT(MONTH FROM f.fine_date) AS month,
       COUNT(*) AS violations_count,
       SUM(f.amount) AS total_amount
FROM fines f
JOIN vehicles v ON f.vehicle_id = v.vehicle_id
JOIN vehicle_brands vb ON v.brand_id = vb.brand_id
GROUP BY vb.brand_name, EXTRACT(MONTH FROM f.fine_date)
ORDER BY vb.brand_name, month;

-- 4. Топ-5 водіїв за кількістю штрафів.
SELECT p.full_name,
       COUNT(f.fine_id) AS fines_count
FROM fines f
JOIN persons p ON f.driver_id = p.person_id
GROUP BY p.full_name
ORDER BY fines_count DESC
LIMIT 5;

-- 5. Історія автомобіля: хто і в який період мав право ним керувати.
SELECT v.vin,
       vb.brand_name,
       v.model,
       p.full_name AS driver_name,
       dp.valid_from,
       dp.valid_to,
       dp.permission_type
FROM driving_permissions dp
JOIN vehicles v ON dp.vehicle_id = v.vehicle_id
JOIN vehicle_brands vb ON v.brand_id = vb.brand_id
JOIN persons p ON dp.person_id = p.person_id
ORDER BY v.vehicle_id, dp.valid_from;

-- 6. Водії, які скоїли ДТП на автомобілі, яким вони не мають права керувати.
SELECT p.full_name AS driver_name,
       v.vin,
       vb.brand_name,
       v.model,
       f.fine_date,
       f.violation_type
FROM fines f
JOIN persons p ON f.driver_id = p.person_id
JOIN vehicles v ON f.vehicle_id = v.vehicle_id
JOIN vehicle_brands vb ON v.brand_id = vb.brand_id
WHERE f.is_accident = TRUE
  AND NOT EXISTS (
      SELECT 1
      FROM driving_permissions dp
      WHERE dp.vehicle_id = f.vehicle_id
        AND dp.person_id = f.driver_id
        AND f.fine_date BETWEEN dp.valid_from AND COALESCE(dp.valid_to, f.fine_date)
  );
