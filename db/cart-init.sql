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
  (2, 'Bob Martin');

-- Data for carts
INSERT INTO carts (id, customer_id) VALUES
  (1, 1),
  (2, 2);

-- Data for cart_items
INSERT INTO cart_items (id, cart_id, product_id, quantity) VALUES
  (1, 1, 1, 2),
  (2, 1, 2, 1),
  (3, 2, 1, 1); 