CREATE TABLE steam_game (
    id BIGSERIAL PRIMARY KEY,
    app_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    steam_url TEXT NOT NULL,
    capsule_image_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_steam_game_app_id UNIQUE (app_id)
);

CREATE TABLE steam_price_snapshot (
    id BIGSERIAL PRIMARY KEY,
    app_id BIGINT NOT NULL,
    original_price NUMERIC(10, 2),
    final_price NUMERIC(10, 2),
    discount_percent INTEGER NOT NULL,
    collected_at TIMESTAMP NOT NULL
);

CREATE TABLE steam_review_summary (
    id BIGSERIAL PRIMARY KEY,
    app_id BIGINT NOT NULL,
    total_reviews INTEGER NOT NULL,
    total_positive INTEGER NOT NULL,
    total_negative INTEGER NOT NULL,
    review_score INTEGER,
    review_score_desc VARCHAR(255),
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_steam_review_summary_app_id UNIQUE (app_id)
);

CREATE INDEX idx_steam_price_snapshot_app_id ON steam_price_snapshot (app_id);
CREATE INDEX idx_steam_price_snapshot_discount_percent ON steam_price_snapshot (discount_percent DESC);
CREATE INDEX idx_steam_price_snapshot_app_id_collected_at_desc
    ON steam_price_snapshot (app_id, collected_at DESC);
CREATE INDEX idx_steam_review_summary_updated_at ON steam_review_summary (updated_at DESC);
