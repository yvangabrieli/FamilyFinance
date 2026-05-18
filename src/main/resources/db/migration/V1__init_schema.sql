-- V1__init_schema.sql
-- Initial database schema for FamilyFinance API
-- Using NUMERIC(19,4) for ALL money fields — never float/double

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Users
CREATE TABLE users (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(100)  NOT NULL,
    email        VARCHAR(255)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Families
CREATE TABLE families (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL,
    created_by UUID         NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Family membership (many-to-many with role)
CREATE TABLE family_members (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID        NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    user_id   UUID        NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    role      VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'MEMBER')),
    joined_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (family_id, user_id)
);

-- Transaction categories
CREATE TABLE categories (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(50) NOT NULL,
    icon       VARCHAR(50),
    type       VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    is_default BOOLEAN     NOT NULL DEFAULT FALSE
);

-- Transactions (the core entity)
CREATE TABLE transactions (
    id          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    amount      NUMERIC(19, 4) NOT NULL CHECK (amount > 0),
    type        VARCHAR(10)    NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    category_id UUID           NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    user_id     UUID           NOT NULL REFERENCES users(id)      ON DELETE RESTRICT,
    family_id   UUID           NOT NULL REFERENCES families(id)   ON DELETE CASCADE,
    note        TEXT,
    date        DATE           NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Budgets (category spending limits per period)
CREATE TABLE budgets (
    id               UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id        UUID           NOT NULL REFERENCES families(id) ON DELETE CASCADE,
    category_id      UUID           NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    limit_amount     NUMERIC(19, 4) NOT NULL CHECK (limit_amount > 0),
    period           VARCHAR(10)    NOT NULL CHECK (period IN ('WEEKLY', 'MONTHLY', 'YEARLY')),
    alert_threshold  INTEGER        NOT NULL DEFAULT 80 CHECK (alert_threshold BETWEEN 1 AND 100),
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    UNIQUE (family_id, category_id)
);

-- Indexes for common query patterns
CREATE INDEX idx_transactions_family_id   ON transactions(family_id);
CREATE INDEX idx_transactions_user_id     ON transactions(user_id);
CREATE INDEX idx_transactions_date        ON transactions(date DESC);
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
CREATE INDEX idx_family_members_user_id   ON family_members(user_id);
CREATE INDEX idx_family_members_family_id ON family_members(family_id);
