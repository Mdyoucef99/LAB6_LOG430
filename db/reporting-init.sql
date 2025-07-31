-- Reporting Service Database Initialization

-- Sequences
CREATE SEQUENCE IF NOT EXISTS sales_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS stores_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE IF NOT EXISTS produits_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

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

CREATE TABLE IF NOT EXISTS stores (
    id integer PRIMARY KEY DEFAULT nextval('stores_id_seq'),
    name character varying(255) NOT NULL
);

-- Data for produits (needed for reporting)
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

-- Data for stores
INSERT INTO stores (id, name) VALUES
  (1, 'Magasin 1'),
  (2, 'Magasin 2'),
  (3, 'Magasin 3'),
  (4, 'Magasin 4'),
  (5, 'Magasin 5'),
  (6, 'Logistique'),
  (7, 'Maison-Mère');

-- Set sequences to correct values
SELECT pg_catalog.setval('sales_id_seq', 9, true);
SELECT pg_catalog.setval('stores_id_seq', 7, true);
SELECT pg_catalog.setval('produits_id_seq', 3, true); 