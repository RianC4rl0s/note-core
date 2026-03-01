-- This migration only creates roles and permissions.
-- User seeding is handled by DataSeeder.java on application startup.

-- Roles (built_in = true, cannot be deleted or edited by users)
INSERT INTO roles (name, description, built_in, created_at, updated_at) VALUES
    ('USER',        'Default user role',        TRUE, NOW(), NOW()),
    ('ADMIN',       'Administrator role',       TRUE, NOW(), NOW()),
    ('SUPER_ADMIN', 'Super administrator role', TRUE, NOW(), NOW());

-- Permissions (built_in = true)
INSERT INTO permissions (name, description, built_in, created_at, updated_at) VALUES
    ('USER_READ',           'Read user information',    TRUE, NOW(), NOW()),
    ('USER_WRITE',          'Write user information',   TRUE, NOW(), NOW()),
    ('ADMIN_IMPERSONATE',   'Impersonate other users',  TRUE, NOW(), NOW());

-- Role-Permission mappings
-- ADMIN gets USER_READ and USER_WRITE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND p.name IN ('USER_READ', 'USER_WRITE');

-- SUPER_ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SUPER_ADMIN';
