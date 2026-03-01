-- Seed BASIC plan (built_in)
INSERT INTO plans (name, description, max_projects, max_pages_per_project, built_in, created_at, updated_at)
VALUES ('BASIC', 'Basic plan with moderate limits', 5, 10, TRUE, NOW(), NOW());

-- Seed PRO plan (built_in)
INSERT INTO plans (name, description, max_projects, max_pages_per_project, built_in, created_at, updated_at)
VALUES ('PRO', 'Pro plan with higher limits', 5, 20, TRUE, NOW(), NOW());
