ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Seed default admin (password: Admin123!)
INSERT INTO users (email, password_hash, full_name, role, created_at, updated_at)
VALUES (
    'admin@harbor.com',
    '$2a$10$qNp9ywgzh/zJrLptbZayf.25V97eOP1QU254U0PvQSA/mIUoGJsaS',
    'Harbor Admin',
    'ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
