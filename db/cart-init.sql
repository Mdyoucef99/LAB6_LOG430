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
INSERT INTO customers (name) VALUES
  ('Alice Dupont'),
  ('Bob Martin'),
  ('Charlie Wilson'),
  ('Diana Smith'),
  ('Eve Johnson'),
  ('Frank Wilson'),
  ('Grace Brown'),
  ('Henry Davis'),
  ('Ivy Miller'),
  ('Jack Taylor');

-- Data for carts (starting fresh) - using customer IDs from the previous insert
INSERT INTO carts (customer_id) VALUES
  (1), (2), (3), (4), (5), (6), (7), (8), (9), (10);

-- Data for cart_items (starting fresh) - using cart IDs from the previous insert
INSERT INTO cart_items (cart_id, product_id, quantity) VALUES
  (1, 1, 2), (1, 2, 1),        -- Alice's cart
  (2, 1, 1),                   -- Bob's cart
  (3, 1, 3), (3, 3, 1),        -- Charlie's cart
  (4, 2, 2),                   -- Diana's cart
  (5, 1, 1), (5, 2, 1),        -- Eve's cart
  (6, 3, 2),                   -- Frank's cart
  (7, 1, 4),                   -- Grace's cart
  (8, 2, 1),                   -- Henry's cart
  (9, 1, 2), (9, 3, 1),        -- Ivy's cart
  (10, 2, 3);                  -- Jack's cart 