CREATE TABLE users (
    id         UUID         PRIMARY KEY,
    sid        BIGINT       GENERATED ALWAYS AS IDENTITY UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    phone      VARCHAR(50),
    birth_date DATE,
    avatar_url VARCHAR(500),
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
