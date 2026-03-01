CREATE TABLE pages (
    id          UUID         PRIMARY KEY,
    sid         BIGINT       GENERATED ALWAYS AS IDENTITY UNIQUE,
    project_id  UUID         NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    page_data   TEXT,
    position    INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_pages_project_id ON pages(project_id);
