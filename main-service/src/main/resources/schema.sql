CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(254) NOT NULL,
    name VARCHAR(250) NOT NULL,
    UNIQUE (email)
);

CREATE INDEX IF NOT EXISTS index_users_email ON users (email);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    UNIQUE (name)
);

CREATE INDEX IF NOT EXISTS index_categories_name ON categories (name);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category BIGINT NOT NULL REFERENCES categories (id),
    initiator BIGINT NOT NULL REFERENCES users (id),
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    lat FLOAT8 NOT NULL,
    lon FLOAT8 NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    participant_limit INTEGER NOT NULL DEFAULT 0,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN NOT NULL DEFAULT TRUE,
    state VARCHAR(9) NOT NULL,
    title VARCHAR(120) NOT NULL
);

CREATE INDEX IF NOT EXISTS index_events_category ON events (category);
CREATE INDEX IF NOT EXISTS index_events_initiator ON events (initiator);
CREATE INDEX IF NOT EXISTS index_events_event_date ON events (event_date);
CREATE INDEX IF NOT EXISTS index_events_paid ON events (paid);
CREATE INDEX IF NOT EXISTS index_events_state ON events (state);

CREATE TABLE IF NOT EXISTS requests (
   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   event BIGINT NOT NULL REFERENCES events (id),
   requester BIGINT NOT NULL REFERENCES users (id),
   status VARCHAR(9) NOT NULL
);

CREATE INDEX IF NOT EXISTS index_requests_event ON requests (event);
CREATE INDEX IF NOT EXISTS index_requests_requester ON requests (requester);
CREATE INDEX IF NOT EXISTS index_requests_status ON requests (status);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    title VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilations_id BIGINT NOT NULL REFERENCES compilations (id),
    event_id BIGINT NOT NULL REFERENCES events (id),
    UNIQUE (compilations_id, event_id)
);