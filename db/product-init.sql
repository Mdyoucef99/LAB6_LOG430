-- Product Service Database Initialization

-- Sequences
CREATE SEQUENCE IF NOT EXISTS produits_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

-- Tables
CREATE TABLE IF NOT EXISTS produits (
    id integer PRIMARY KEY DEFAULT nextval('produits_id_seq'),
    nom character varying(255),
    categorie character varying(255),
    prix double precision,
    quantite integer
);

-- Data for produits
INSERT INTO produits (id, nom, categorie, prix, quantite) VALUES
  (2, 'Lait', 'Nourriture', 1.8, 50),
  (3, 'Savon', 'Hygiène', 3.2, 30),
  (1, 'Pain', 'Nourriture', 2.5, 83),
  (0, 'Laptop Dell', 'Informatique', 999.99, 25);

-- Set sequences to correct values
SELECT pg_catalog.setval('produits_id_seq', 3, true); 