CREATE TABLE plans (
    id                     BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                   VARCHAR(255) NOT NULL UNIQUE,
    description            VARCHAR(255),
    max_projects           INT          NOT NULL DEFAULT 10,
    max_pages_per_project  INT          NOT NULL DEFAULT 50,
    built_in               BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITH TIME ZONE NOT NULL
);
