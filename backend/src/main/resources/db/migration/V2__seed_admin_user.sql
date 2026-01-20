-- VetFlow - Seed Admin User
-- Flyway Migration: V2__seed_admin_user.sql
-- Creates default admin user for initial system access

-- Insert default admin user (password: Vetflow#2024)
-- Password hash generated with BCrypt strength 10
INSERT INTO system_users (username, email, password_hash, role, is_active, created_at, updated_at)
VALUES (
    'admin',
    'admin@vetflow.test',
    '$2b$10$RDWS5TPPOW5J6iKIHOScI.5JgQZd/yHkSXOcQMuBHyyPwx52n6dd6',
    'admin',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

-- Insert a veterinarian user for testing (password: Vetflow#2024)
INSERT INTO system_users (username, email, password_hash, role, is_active, created_at, updated_at)
VALUES (
    'drsmith',
    'drsmith@vetflow.test',
    '$2b$10$RDWS5TPPOW5J6iKIHOScI.5JgQZd/yHkSXOcQMuBHyyPwx52n6dd6',
    'veterinarian',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

-- Insert an assistant user for testing (password: Vetflow#2024)
INSERT INTO system_users (username, email, password_hash, role, is_active, created_at, updated_at)
VALUES (
    'assistant',
    'assistant@vetflow.test',
    '$2b$10$RDWS5TPPOW5J6iKIHOScI.5JgQZd/yHkSXOcQMuBHyyPwx52n6dd6',
    'assistant',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;
