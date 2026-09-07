UPDATE organizations
SET name = 'Hold Demo',
    slug = 'hold',
    updated_at = CURRENT_TIMESTAMP
WHERE slug = 'harbor';

UPDATE users
SET email = 'admin@hold.com',
    full_name = 'Hold Admin',
    updated_at = CURRENT_TIMESTAMP
WHERE email = 'admin@harbor.com';

UPDATE resources
SET name = REPLACE(name, 'Harbor', 'Hold'),
    updated_at = CURRENT_TIMESTAMP
WHERE name LIKE 'Harbor%';
