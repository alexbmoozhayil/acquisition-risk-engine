CREATE TABLE agencies (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE vendors (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE contracts (
    id BIGSERIAL PRIMARY KEY,
    award_id TEXT NOT NULL UNIQUE,
    vendor_id BIGINT NOT NULL REFERENCES vendors(id),
    agency_id BIGINT NOT NULL REFERENCES agencies(id),
    award_amount NUMERIC(18, 2),
    start_date DATE,
    end_date DATE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);