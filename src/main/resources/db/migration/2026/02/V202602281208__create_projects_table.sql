CREATE TABLE projects (
    id          UUID         PRIMARY KEY,
    sid         BIGINT       GENERATED ALWAYS AS IDENTITY UNIQUE,
    user_id     UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_projects_user_id ON projects(user_id);
