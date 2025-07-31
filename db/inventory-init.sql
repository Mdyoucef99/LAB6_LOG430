-- Inventory Service Database Initialization

-- Sequences
CREATE SEQUENCE IF NOT EXISTS stocks_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
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

CREATE TABLE IF NOT EXISTS stores (
    id integer PRIMARY KEY DEFAULT nextval('stores_id_seq'),
    name character varying(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS stocks (
    id integer PRIMARY KEY DEFAULT nextval('stocks_id_seq'),
    store_id integer NOT NULL,
    product_id integer NOT NULL,
    quantity integer NOT NULL
);

-- Data for produits (needed for inventory relationships)
INSERT INTO produits (id, nom, categorie, prix, quantite) VALUES
  (2, 'Lait', 'Nourriture', 1.8, 50),
  (3, 'Savon', 'Hygiène', 3.2, 30),
  (1, 'Pain', 'Nourriture', 2.5, 83),
  (0, 'Laptop Dell', 'Informatique', 999.99, 25);

-- Data for stores
INSERT INTO stores (id, name) VALUES
  (1, 'Magasin 1'),
  (2, 'Magasin 2'),
  (3, 'Magasin 3'),
  (4, 'Magasin 4'),
  (5, 'Magasin 5'),
  (6, 'Logistique'),
  (7, 'Maison-Mère');

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

-- Set sequences to correct values
SELECT pg_catalog.setval('stocks_id_seq', 21, true);
SELECT pg_catalog.setval('stores_id_seq', 7, true);
SELECT pg_catalog.setval('produits_id_seq', 3, true); 