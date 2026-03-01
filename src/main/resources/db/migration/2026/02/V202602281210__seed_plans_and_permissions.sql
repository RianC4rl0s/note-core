-- Seed FREE plan (built_in)
INSERT INTO plans (name, description, max_projects, max_pages_per_project, built_in, created_at, updated_at)
VALUES ('FREE', 'Free plan with basic limits', 3, 5, TRUE, NOW(), NOW());

-- Seed PLAN permissions (built_in)
INSERT INTO permissions (name, description, built_in, created_at, updated_at) VALUES
    ('PLAN_READ',   'Read plan information',  TRUE, NOW(), NOW()),
    ('PLAN_WRITE',  'Write plan information',  TRUE, NOW(), NOW()),
    ('PLAN_ASSIGN', 'Assign plans to users',   TRUE, NOW(), NOW());

-- Assign PLAN permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND p.name IN ('PLAN_READ', 'PLAN_WRITE', 'PLAN_ASSIGN');

-- Assign PLAN permissions to SUPER_ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SUPER_ADMIN' AND p.name IN ('PLAN_READ', 'PLAN_WRITE', 'PLAN_ASSIGN');
