-- Event Store Database Initialization

-- Sequences
CREATE SEQUENCE IF NOT EXISTS events_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

-- Event Store for Event Sourcing
CREATE TABLE IF NOT EXISTS event_store (
    id bigint PRIMARY KEY DEFAULT nextval('events_id_seq'),
    event_id varchar(255) NOT NULL UNIQUE,
    event_type varchar(255) NOT NULL,
    aggregate_id varchar(255) NOT NULL,
    aggregate_type varchar(255) NOT NULL,
    payload text NOT NULL,
    saga_id varchar(255),
    timestamp timestamp NOT NULL DEFAULT now(),
    version integer NOT NULL DEFAULT 1
);

-- Indexes for Event Store
CREATE INDEX IF NOT EXISTS idx_event_store_aggregate_id ON event_store(aggregate_id);
CREATE INDEX IF NOT EXISTS idx_event_store_event_type ON event_store(event_type);
CREATE INDEX IF NOT EXISTS idx_event_store_saga_id ON event_store(saga_id);
CREATE INDEX IF NOT EXISTS idx_event_store_timestamp ON event_store(timestamp);
CREATE INDEX IF NOT EXISTS idx_event_store_aggregate_type ON event_store(aggregate_type);

-- Set sequence to correct value
SELECT pg_catalog.setval('events_id_seq', 1, true); 