CREATE TABLE organizations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

INSERT INTO organizations (name, slug, created_at, updated_at)
VALUES ('Harbor Demo', 'harbor', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE users
    ADD COLUMN organization_id BIGINT REFERENCES organizations (id);

ALTER TABLE resources
    ADD COLUMN organization_id BIGINT REFERENCES organizations (id);

ALTER TABLE bookings
    ADD COLUMN organization_id BIGINT REFERENCES organizations (id);

ALTER TABLE invoices
    ADD COLUMN organization_id BIGINT REFERENCES organizations (id);

UPDATE users SET organization_id = (SELECT id FROM organizations WHERE slug = 'harbor');
UPDATE resources SET organization_id = (SELECT id FROM organizations WHERE slug = 'harbor');
UPDATE bookings SET organization_id = (SELECT id FROM organizations WHERE slug = 'harbor');
UPDATE invoices SET organization_id = (SELECT id FROM organizations WHERE slug = 'harbor');

ALTER TABLE users ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE resources ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE bookings ALTER COLUMN organization_id SET NOT NULL;
ALTER TABLE invoices ALTER COLUMN organization_id SET NOT NULL;

CREATE INDEX idx_users_organization_id ON users (organization_id);
CREATE INDEX idx_resources_organization_id ON resources (organization_id);
CREATE INDEX idx_bookings_organization_id ON bookings (organization_id);
CREATE INDEX idx_invoices_organization_id ON invoices (organization_id);
