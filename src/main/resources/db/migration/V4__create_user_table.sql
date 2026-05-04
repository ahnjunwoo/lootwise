CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    nickname VARCHAR(30) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'USER',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    withdrawn_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('USER')),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'WITHDRAWN')),
    CONSTRAINT chk_users_withdrawn_at CHECK (
        (status = 'WITHDRAWN' AND withdrawn_at IS NOT NULL)
        OR (status = 'ACTIVE' AND withdrawn_at IS NULL)
    )
);
