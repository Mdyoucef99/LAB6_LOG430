-- Order Service Database Initialization

-- Tables
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id),
    total_amount DOUBLE PRECISION NOT NULL,
    order_date TIMESTAMP NOT NULL
);

-- Data for customers (needed for order relationships)
INSERT INTO customers (id, name) VALUES
  (1, 'Alice Dupont'),
  (2, 'Bob Martin');

-- Data for orders
INSERT INTO orders (id, customer_id, total_amount, order_date) VALUES
  (1, 1, 6.1, '2025-07-14 22:00:00'),
  (2, 2, 2.5, '2025-07-14 22:30:00'),
  (3, 1, 45.75, '2025-01-27 10:15:00'),
  (4, 2, 125.99, '2025-01-27 11:30:00'),
  (5, 1, 12.50, '2025-01-27 14:45:00'),
  (6, 2, 89.25, '2025-01-27 16:20:00'),
  (7, 1, 33.80, '2025-01-27 18:10:00'),
  (8, 2, 67.45, '2025-01-27 20:30:00'); 