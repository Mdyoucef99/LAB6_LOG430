-- PostgreSQL database initialization (merged)

-- Sequences
CREATE SEQUENCE IF NOT EXISTS produits_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS sales_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS stocks_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS stores_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

-- Tables
CREATE TABLE IF NOT EXISTS produits (
    id integer PRIMARY KEY DEFAULT nextval('produits_id_seq'),
    nom character varying(255),
    categorie character varying(255),
    prix double precision,
    quantite integer
);

CREATE TABLE IF NOT EXISTS sales (
    id integer PRIMARY KEY DEFAULT nextval('sales_id_seq'),
    store_id integer NOT NULL,
    product_id integer NOT NULL,
    quantity integer NOT NULL,
    "saleDate" timestamp without time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS stocks (
    id integer PRIMARY KEY DEFAULT nextval('stocks_id_seq'),
    store_id integer NOT NULL,
    product_id integer NOT NULL,
    quantity integer NOT NULL
);

CREATE TABLE IF NOT EXISTS stores (
    id integer PRIMARY KEY DEFAULT nextval('stores_id_seq'),
    name character varying(255) NOT NULL
);

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

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customers(id),
    total_amount DOUBLE PRECISION NOT NULL,
    order_date TIMESTAMP NOT NULL
);

-- Data for produits
INSERT INTO produits (id, nom, categorie, prix, quantite) VALUES
  (2, 'Lait', 'Nourriture', 1.8, 50),
  (3, 'Savon', 'Hygiène', 3.2, 30),
  (1, 'Pain', 'Nourriture', 2.5, 83),
  (0, 'Laptop Dell', 'Informatique', 999.99, 25);

-- Data for sales
INSERT INTO sales (id, store_id, product_id, quantity, "saleDate") VALUES
  (1, 1, 1, 16, '2025-06-07 19:58:51.667'),
  (2, 1, 1, 1, '2025-06-08 10:42:25.191'),
  (3, 1, 1, 73, '2025-06-08 11:21:27.3'),
  (4, 1, 1, 300, '2025-06-08 11:58:19.747'),
  (5, 1, 1, 1, '2025-06-08 11:58:43.676'),
  (6, 1, 1, 71, '2025-06-08 12:07:19.386'),
  (7, 1, 3, 30, '2025-06-08 12:08:04.745'),
  (8, 1, 1, 1, '2025-06-08 12:47:51.064'),
  (9, 1, 1, 30, '2025-06-08 12:53:44.537');

-- Data for stocks
INSERT INTO stocks (id, store_id, product_id, quantity) VALUES
  (2, 1, 2, 1603),
  (3, 1, 3, 930),
  (5, 2, 2, 1600),
  (6, 2, 3, 960),
  (4, 2, 1, 2837),
  (8, 3, 2, 1600),
  (9, 3, 3, 960),
  (7, 3, 1, 2837),
  (11, 4, 2, 1600),
  (12, 4, 3, 960),
  (10, 4, 1, 2837),
  (14, 5, 2, 1600),
  (15, 5, 3, 960),
  (13, 5, 1, 2837),
  (17, 6, 2, 1597),
  (18, 6, 3, 960),
  (20, 7, 2, 1600),
  (21, 7, 3, 960),
  (19, 7, 1, 2837),
  (16, 6, 1, 2835),
  (1, 1, 1, 2353);

-- Data for stores
INSERT INTO stores (id, name) VALUES
  (1, 'Magasin 1'),
  (2, 'Magasin 2'),
  (3, 'Magasin 3'),
  (4, 'Magasin 4'),
  (5, 'Magasin 5'),
  (6, 'Logistique'),
  (7, 'Maison-Mère');

-- Data for customers
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

-- Set sequences to correct values
SELECT pg_catalog.setval('produits_id_seq', 3, true);
SELECT pg_catalog.setval('sales_id_seq', 9, true);
SELECT pg_catalog.setval('stocks_id_seq', 21, true);
SELECT pg_catalog.setval('stores_id_seq', 7, true);
