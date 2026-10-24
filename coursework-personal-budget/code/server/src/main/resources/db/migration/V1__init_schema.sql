-- ============================================================
-- V1__init_schema.sql
-- Initial schema for Personal Finance Management Platform
-- ============================================================

-- ============================================================
-- 1. ACCOUNTS (authentication)
-- ============================================================
CREATE TABLE IF NOT EXISTS accounts (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(10)  NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 2. SIGN-UP KEYS (email OTP for registration)
-- ============================================================
CREATE TABLE IF NOT EXISTS sign_up_keys (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(255) NOT NULL,
    reg_key     VARCHAR(8)   NOT NULL,
    role        VARCHAR(10)  NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 3. PROFILES
-- ============================================================
CREATE TABLE IF NOT EXISTS profiles (
    id               BIGSERIAL PRIMARY KEY,
    account_id       BIGINT       NOT NULL UNIQUE REFERENCES accounts(id),
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    phone            VARCHAR(20),
    birth_date       DATE,
    friend_code      VARCHAR(12)  NOT NULL UNIQUE,
    default_currency VARCHAR(3)   NOT NULL DEFAULT 'BYN',
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 4. PRIVACY SETTINGS (1:1 with profiles)
-- ============================================================
CREATE TABLE IF NOT EXISTS privacy_settings (
    id                       BIGSERIAL PRIMARY KEY,
    profile_id               BIGINT  NOT NULL UNIQUE REFERENCES profiles(id),
    searchable_by_email      BOOLEAN NOT NULL DEFAULT TRUE,
    searchable_by_phone      BOOLEAN NOT NULL DEFAULT FALSE,
    searchable_by_friend_code BOOLEAN NOT NULL DEFAULT TRUE,
    show_full_name           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at               TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 5. NOTIFICATION SETTINGS (1:1 with profiles)
-- ============================================================
CREATE TABLE IF NOT EXISTS notification_settings (
    id                     BIGSERIAL PRIMARY KEY,
    profile_id             BIGINT  NOT NULL UNIQUE REFERENCES profiles(id),
    on_goal_reached        BOOLEAN NOT NULL DEFAULT TRUE,
    on_budget_exceeded     BOOLEAN NOT NULL DEFAULT TRUE,
    on_shared_transaction  BOOLEAN NOT NULL DEFAULT TRUE,
    on_contact_request     BOOLEAN NOT NULL DEFAULT TRUE,
    on_deposit_matured     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at             TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 6. CONTACTS (mutual friendship)
-- ============================================================
CREATE TABLE IF NOT EXISTS contacts (
    id           BIGSERIAL PRIMARY KEY,
    requester_id BIGINT      NOT NULL REFERENCES profiles(id),
    addressee_id BIGINT      NOT NULL REFERENCES profiles(id),
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                     CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED')),
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_contacts_requester_addressee UNIQUE (requester_id, addressee_id),
    CONSTRAINT chk_contacts_no_self CHECK (requester_id <> addressee_id)
);

-- ============================================================
-- 7. CATEGORIES
-- ============================================================
CREATE TABLE IF NOT EXISTS categories (
    id         BIGSERIAL PRIMARY KEY,
    profile_id BIGINT      NOT NULL REFERENCES profiles(id),
    name       VARCHAR(100) NOT NULL,
    color      VARCHAR(7),
    type       VARCHAR(10)  NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 8. TRANSACTIONS
-- ============================================================
CREATE TABLE IF NOT EXISTS transactions (
    id                   BIGSERIAL PRIMARY KEY,
    profile_id           BIGINT          NOT NULL REFERENCES profiles(id),
    category_id          BIGINT          REFERENCES categories(id),
    amount               NUMERIC(19, 4)  NOT NULL,
    currency             VARCHAR(3)      NOT NULL,
    exchange_rate_to_byn NUMERIC(19, 6)  NOT NULL,
    amount_byn           NUMERIC(19, 4)  NOT NULL,
    type                 VARCHAR(10)     NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    date                 DATE            NOT NULL,
    description          TEXT,
    deleted              BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transactions_profile_date ON transactions(profile_id, date);
CREATE INDEX idx_transactions_profile_type ON transactions(profile_id, type);

-- ============================================================
-- 9. GOALS
-- ============================================================
CREATE TABLE IF NOT EXISTS goals (
    id             BIGSERIAL PRIMARY KEY,
    profile_id     BIGINT         NOT NULL REFERENCES profiles(id),
    name           VARCHAR(200)   NOT NULL,
    target_amount  NUMERIC(19, 4) NOT NULL,
    currency       VARCHAR(3)     NOT NULL,
    current_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    deadline       DATE,
    status         VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE'
                       CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    deleted        BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 10. PLANNED EXPENSES (budget limits + planned purchases)
-- ============================================================
CREATE TABLE IF NOT EXISTS planned_expenses (
    id           BIGSERIAL PRIMARY KEY,
    profile_id   BIGINT         NOT NULL REFERENCES profiles(id),
    category_id  BIGINT         REFERENCES categories(id),
    name         VARCHAR(200)   NOT NULL,
    amount       NUMERIC(19, 4) NOT NULL,
    currency     VARCHAR(3)     NOT NULL,
    type         VARCHAR(20)    NOT NULL CHECK (type IN ('BUDGET_LIMIT', 'PLANNED_PURCHASE')),
    period_type  VARCHAR(15)    CHECK (period_type IN ('MONTHLY', 'QUARTERLY', 'YEARLY')),
    planned_date DATE,
    is_fulfilled BOOLEAN        NOT NULL DEFAULT FALSE,
    deleted      BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 11. DEPOSITS
-- ============================================================
CREATE TABLE IF NOT EXISTS deposits (
    id                    BIGSERIAL PRIMARY KEY,
    profile_id            BIGINT         NOT NULL REFERENCES profiles(id),
    name                  VARCHAR(200)   NOT NULL,
    type                  VARCHAR(15)    NOT NULL CHECK (type IN ('DEPOSIT', 'PIGGY_BANK')),
    initial_amount        NUMERIC(19, 4) NOT NULL,
    currency              VARCHAR(3)     NOT NULL,
    interest_rate         NUMERIC(5, 2),
    start_date            DATE           NOT NULL,
    end_date              DATE,
    current_amount        NUMERIC(19, 4) NOT NULL,
    status                VARCHAR(15)    NOT NULL DEFAULT 'ACTIVE'
                              CHECK (status IN ('ACTIVE', 'COMPLETED')),
    income_transaction_id BIGINT         REFERENCES transactions(id),
    deleted               BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 12. SHARED SHEETS
-- ============================================================
CREATE TABLE IF NOT EXISTS shared_sheets (
    id           BIGSERIAL PRIMARY KEY,
    creator_id   BIGINT      NOT NULL REFERENCES profiles(id),
    name         VARCHAR(200) NOT NULL,
    description  TEXT,
    status       VARCHAR(15)  NOT NULL DEFAULT 'ACTIVE'
                     CHECK (status IN ('ACTIVE', 'COMPLETED')),
    completed_at TIMESTAMP,
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 13. SHARED SHEET MEMBERS
-- ============================================================
CREATE TABLE IF NOT EXISTS shared_sheet_members (
    id         BIGSERIAL PRIMARY KEY,
    sheet_id   BIGINT    NOT NULL REFERENCES shared_sheets(id),
    profile_id BIGINT    NOT NULL REFERENCES profiles(id),
    joined_at  TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_sheet_member UNIQUE (sheet_id, profile_id)
);

-- ============================================================
-- 14. SHARED TRANSACTIONS
-- ============================================================
CREATE TABLE IF NOT EXISTS shared_transactions (
    id                   BIGSERIAL PRIMARY KEY,
    sheet_id             BIGINT         NOT NULL REFERENCES shared_sheets(id),
    profile_id           BIGINT         NOT NULL REFERENCES profiles(id),
    amount               NUMERIC(19, 4) NOT NULL,
    currency             VARCHAR(3)     NOT NULL,
    exchange_rate_to_byn NUMERIC(19, 6) NOT NULL,
    amount_byn           NUMERIC(19, 4) NOT NULL,
    type                 VARCHAR(15)    NOT NULL CHECK (type IN ('EXPENSE', 'CONTRIBUTION')),
    description          TEXT,
    date                 DATE           NOT NULL,
    deleted              BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_shared_transactions_sheet ON shared_transactions(sheet_id);

-- ============================================================
-- 15. SHARED GOALS
-- ============================================================
CREATE TABLE IF NOT EXISTS shared_goals (
    id             BIGSERIAL PRIMARY KEY,
    sheet_id       BIGINT         NOT NULL REFERENCES shared_sheets(id),
    name           VARCHAR(200)   NOT NULL,
    target_amount  NUMERIC(19, 4) NOT NULL,
    currency       VARCHAR(3)     NOT NULL,
    current_amount NUMERIC(19, 4) NOT NULL DEFAULT 0,
    deadline       DATE,
    status         VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE'
                       CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    deleted        BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 16. RESOURCE CATEGORIES
-- ============================================================
CREATE TABLE IF NOT EXISTS resource_categories (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 17. FINANCIAL RESOURCES
-- ============================================================
CREATE TABLE IF NOT EXISTS financial_resources (
    id                   BIGSERIAL PRIMARY KEY,
    author_id            BIGINT       NOT NULL REFERENCES accounts(id),
    resource_category_id BIGINT       NOT NULL REFERENCES resource_categories(id),
    title                VARCHAR(300) NOT NULL,
    content              TEXT         NOT NULL,
    deleted              BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 18. RESOURCE ATTACHMENTS
-- ============================================================
CREATE TABLE IF NOT EXISTS resource_attachments (
    id          BIGSERIAL PRIMARY KEY,
    resource_id BIGINT       NOT NULL REFERENCES financial_resources(id),
    file_name   VARCHAR(255) NOT NULL,
    file_path   VARCHAR(500) NOT NULL,
    file_type   VARCHAR(10)  NOT NULL CHECK (file_type IN ('IMAGE', 'DOCUMENT')),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
