-- Platform SUPER_ADMIN users are not tied to a tenant org.
ALTER TABLE users ALTER COLUMN organization_id DROP NOT NULL;

-- Seed platform super admin (password: SuperAdmin123!)
INSERT INTO users (email, password_hash, full_name, role, organization_id, created_at, updated_at)
VALUES (
    'superadmin@hold.com',
    '$2a$10$EuWTQ3M5hb9nXaih3XJlM.v2H5HP29T6yccIaAv28UAFPcRZudGG2',
    'Hold Super Admin',
    'SUPER_ADMIN',
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
