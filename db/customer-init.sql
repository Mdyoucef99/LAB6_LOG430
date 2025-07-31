-- Customer Service Database Initialization

-- Tables
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Data for customers
INSERT INTO customers (id, name) VALUES
  (1, 'Alice Dupont'),
  (2, 'Bob Martin'); 