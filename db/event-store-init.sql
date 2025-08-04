-- Event Store Database Initialization

-- Events table (compatible with ORMLite)
CREATE TABLE IF NOT EXISTS events (
    id BIGINT PRIMARY KEY,
    event_id varchar(255) NOT NULL,
    event_type varchar(255) NOT NULL,
    aggregate_id varchar(255) NOT NULL,
    event_data text NOT NULL,
    timestamp timestamp NOT NULL DEFAULT now(),
    version integer NOT NULL DEFAULT 1
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_events_aggregate_id ON events(aggregate_id);
CREATE INDEX IF NOT EXISTS idx_events_event_type ON events(event_type);
CREATE INDEX IF NOT EXISTS idx_events_timestamp ON events(timestamp);
CREATE INDEX IF NOT EXISTS idx_events_version ON events(aggregate_id, version);

-- Sample events for testing
INSERT INTO events (event_id, event_type, aggregate_id, event_data, timestamp, version) VALUES
('event-001', 'ArticleAjoute', 'cart-1', '{"cartId":1,"customerId":1,"productId":1,"productName":"Product-1","quantity":2,"price":10.0}', now(), 1),
('event-002', 'ArticleAjoute', 'cart-1', '{"cartId":1,"customerId":1,"productId":2,"productName":"Product-2","quantity":1,"price":15.0}', now(), 2),
('event-003', 'CartCleared', 'cart-1', '{"cartId":1,"customerId":1}', now(), 3),
('event-004', 'ArticleAjoute', 'cart-2', '{"cartId":2,"customerId":2,"productId":3,"productName":"Product-3","quantity":3,"price":8.0}', now(), 1); 