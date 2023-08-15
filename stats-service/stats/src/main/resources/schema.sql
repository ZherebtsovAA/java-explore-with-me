CREATE TABLE IF NOT EXISTS hits (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    app VARCHAR(30) NOT NULL,
    uri VARCHAR(30) NOT NULL,
    ip VARCHAR(15) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);