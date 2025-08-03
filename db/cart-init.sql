-- Cart Service Database Initialization

-- Tables
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS carts (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS cart_items (
    id SERIAL PRIMARY KEY,
    cart_id INTEGER NOT NULL REFERENCES carts(id),
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL
);

-- Data for customers (needed for cart relationships)
INSERT INTO customers (id, name) VALUES
  (1, 'Alice Dupont'),
  (2, 'Bob Martin'),
  (3, 'Charlie Wilson'),
  (4, 'Diana Smith'),
  (5, 'Eve Johnson'),
  (6, 'Frank Wilson'),
  (7, 'Grace Brown'),
  (8, 'Henry Davis'),
  (9, 'Ivy Miller'),
  (10, 'Jack Taylor');

-- Data for carts (starting fresh)
INSERT INTO carts (id, customer_id) VALUES
  (1, 1),
  (2, 2),
  (3, 3),
  (4, 4),
  (5, 5),
  (6, 6),
  (7, 7),
  (8, 8),
  (9, 9),
  (10, 10);

-- Data for cart_items (starting fresh)
INSERT INTO cart_items (id, cart_id, product_id, quantity) VALUES
  (1, 1, 1, 2),
  (2, 1, 2, 1),
  (3, 2, 1, 1),
  (4, 3, 1, 3),
  (5, 3, 3, 1),
  (6, 4, 2, 2),
  (7, 5, 1, 1),
  (8, 5, 2, 1),
  (9, 6, 3, 2),
  (10, 7, 1, 4),
  (11, 8, 2, 1),
  (12, 9, 1, 2),
  (13, 9, 3, 1),
  (14, 10, 2, 3);

-- Reset sequences to match the inserted data
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));
SELECT setval('carts_id_seq', (SELECT MAX(id) FROM carts));
SELECT setval('cart_items_id_seq', (SELECT MAX(id) FROM cart_items)); 