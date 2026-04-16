CREATE TABLE deals (
    id BIGSERIAL PRIMARY KEY,
    steam_app_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    original_price NUMERIC(10, 2) NOT NULL,
    discounted_price NUMERIC(10, 2) NOT NULL,
    discount_percent INTEGER NOT NULL,
    currency VARCHAR(10) NOT NULL,
    deal_url TEXT NOT NULL,
    collected_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deals_discount_percent ON deals (discount_percent DESC);
CREATE INDEX idx_deals_collected_at ON deals (collected_at DESC);
